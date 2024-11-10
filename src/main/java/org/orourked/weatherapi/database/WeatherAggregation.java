package org.orourked.weatherapi.database;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherAggregation {
  @Autowired private MongoTemplate mongoTemplate;

  public List<Map> queryWeatherData(
      List<String> sensorIds,
      List<String> metrics,
      List<String> stats,
      LocalDate startDate,
      LocalDate endDate) {

    AggregationOperation matchOperation = createMatchOperation(sensorIds);
    AggregationOperation dateFilterOperation = createDateFilterOperation(startDate, endDate);
    SortOperation sortOperation = createSortOperation();
    GroupOperation groupOperation = createGroupOperation(metrics, stats, startDate, endDate);
    ProjectionOperation projectOperation =
        createProjectionOperation(metrics, stats, startDate, endDate);

    // Build the aggregation pipeline
    Aggregation aggregation =
        buildAggregationPipeline(
            matchOperation,
            dateFilterOperation,
            sortOperation,
            groupOperation,
            projectOperation,
            startDate,
            endDate);

    // Execute the aggregation
    AggregationResults<Map> results =
        mongoTemplate.aggregate(aggregation, "weatherData", Map.class);
    return results.getMappedResults();
  }

  private AggregationOperation createMatchOperation(List<String> sensorIds) {
    return Aggregation.match(Criteria.where("sensorId").in(sensorIds));
  }

  private AggregationOperation createDateFilterOperation(LocalDate startDate, LocalDate endDate) {
    if (startDate != null && endDate != null) {
      return Aggregation.match(Criteria.where("timestamp").gte(startDate).lte(endDate));
    }
    return null;
  }

  private SortOperation createSortOperation() {
    return Aggregation.sort(Sort.by(Sort.Direction.DESC, "timestamp"));
  }

  private GroupOperation createGroupOperation(
      List<String> metrics, List<String> stats, LocalDate startDate, LocalDate endDate) {
    GroupOperation groupOperation;
    if (startDate != null && endDate != null) {
      groupOperation = Aggregation.group("sensorId");
      for (String metric : metrics) {
        for (String stat : stats) {
          switch (stat) {
            case "average":
              groupOperation = groupOperation.avg(metric).as("avg" + metric);
              break;
            case "max":
              groupOperation = groupOperation.max(metric).as("max" + metric);
              break;
            case "min":
              groupOperation = groupOperation.min(metric).as("min" + metric);
              break;
            case "sum":
              groupOperation = groupOperation.sum(metric).as("sum" + metric);
              break;
            default:
              throw new IllegalArgumentException("Unknown stat: " + stat);
          }
        }
      }
    } else {
      groupOperation = Aggregation.group("sensorId").first("sensorId").as("sensorId");
      for (String metric : metrics) {
        groupOperation = groupOperation.first(metric).as(metric);
      }
      groupOperation = groupOperation.first("timestamp").as("timestamp");
    }
    return groupOperation;
  }

  private ProjectionOperation createProjectionOperation(
      List<String> metrics, List<String> stats, LocalDate startDate, LocalDate endDate) {
    ProjectionOperation projectOperation = Aggregation.project("sensorId", "timestamp");
    for (String metric : metrics) {
      if (startDate != null && endDate != null) {
        for (String stat : stats) {
          switch (stat) {
            case "average":
              projectOperation =
                  projectOperation
                      .andExpression("round(avg" + metric + " * 10) / 10")
                      .as("avg" + metric);
              break;
            case "max":
              projectOperation =
                  projectOperation
                      .andExpression("round(max" + metric + " * 10) / 10")
                      .as("max" + metric);
              break;
            case "min":
              projectOperation =
                  projectOperation
                      .andExpression("round(min" + metric + " * 10) / 10")
                      .as("min" + metric);
              break;
            case "sum":
              projectOperation =
                  projectOperation
                      .andExpression("round(sum" + metric + " * 10) / 10")
                      .as("sum" + metric);
              break;
            default:
              throw new IllegalArgumentException("Unknown stat: " + stat);
          }
        }
      } else {
        projectOperation =
            projectOperation.andExpression("round(" + metric + " * 10) / 10").as(metric);
      }
    }
    return projectOperation;
  }

  private Aggregation buildAggregationPipeline(
      AggregationOperation matchOperation,
      AggregationOperation dateFilterOperation,
      SortOperation sortOperation,
      GroupOperation groupOperation,
      ProjectionOperation projectOperation,
      LocalDate startDate,
      LocalDate endDate) {

    // Define a sort operation for sensorId as the final step
    SortOperation finalSortOperation = Aggregation.sort(Sort.by(Sort.Direction.ASC, "sensorId"));

    if (startDate != null && endDate != null) {
      return Aggregation.newAggregation(
          matchOperation,
          dateFilterOperation,
          groupOperation,
          projectOperation,
          finalSortOperation);
    } else {
      return Aggregation.newAggregation(
          matchOperation, sortOperation, groupOperation, projectOperation, finalSortOperation);
    }
  }
}
