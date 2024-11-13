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

  /**
   * Queries weather data based on: one, more or all sensor IDs metrics such as temperature
   * statistics such as min, max or average date range. Builds an aggregation pipeline to filter,
   * sort, group, and project data for a MongoDB collection.
   *
   * @param sensorIds List of sensor IDs to include in the results.
   * @param metrics List of metrics (e.g., temperature, humidity) to return per sensor.
   * @param stats List of statistics (e.g., average, max, min, sum) to apply to the metrics.
   * @param startDate Start date for the query.
   * @param endDate End date for the query.
   * @return List of results mapped as a list of key-value pairs.
   */
  public List<Map> queryWeatherData(
      List<String> sensorIds,
      List<String> metrics,
      List<String> stats,
      LocalDate startDate,
      LocalDate endDate) {

    // Define the various stages of the aggregation pipeline.
    AggregationOperation matchOperation = createMatchOperation(sensorIds);
    AggregationOperation dateFilterOperation = createDateFilterOperation(startDate, endDate);
    SortOperation sortOperation = createSortOperation();
    GroupOperation groupOperation = createGroupOperation(metrics, stats, startDate, endDate);
    ProjectionOperation projectOperation =
        createProjectionOperation(metrics, stats, startDate, endDate);

    // Build the aggregation pipeline.
    Aggregation aggregation =
        buildAggregationPipeline(
            matchOperation,
            dateFilterOperation,
            sortOperation,
            groupOperation,
            projectOperation,
            startDate,
            endDate);

    // Execute the aggregation.
    AggregationResults<Map> results =
        mongoTemplate.aggregate(aggregation, "weatherData", Map.class);
    return results.getMappedResults();
  }

  /**
   * Creates an aggregation operation to filter results by sensor IDs.
   *
   * @param sensorIds List of sensor IDs to filter results.
   * @return aggregation operation filtered by sensor IDs.
   */
  private AggregationOperation createMatchOperation(List<String> sensorIds) {
    return Aggregation.match(Criteria.where("sensorId").in(sensorIds));
  }

  /**
   * Creates an aggregation operation to filter results within a start and end date.
   *
   * @param startDate Start date for the query.
   * @param endDate End date for the query.
   * @return aggregation operation filtered by timestamp.
   */
  private AggregationOperation createDateFilterOperation(LocalDate startDate, LocalDate endDate) {
    if (startDate != null && endDate != null) {
      return Aggregation.match(Criteria.where("timestamp").gte(startDate).lte(endDate));
    }
    return null;
  }

  /**
   * Creates a sort operation to sort the query results by timestamp in descending order.
   *
   * @return Sort operation for timestamp field in descending order.
   */
  private SortOperation createSortOperation() {
    return Aggregation.sort(Sort.by(Sort.Direction.DESC, "timestamp"));
  }

  /**
   * Creates a group operation to aggregate metrics based on specified statistics (average, max,
   * min, sum). Groups results by sensorId and computes the chosen statistics for each metric.
   *
   * @param metrics List of metrics to aggregate.
   * @param stats List of statistics to compute.
   * @param startDate Start date for the query.
   * @param endDate End date for the query.
   * @return Group operation with aggregations for each metric and statistic.
   */
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

  /**
   * Creates a projection operation to include only the specified fields in the final results.
   * Computes statistics with rounding to one decimal place.
   *
   * @param metrics List of metrics to include.
   * @param stats List of statistics to project.
   * @param startDate Start date of the query range.
   * @param endDate End date of the query range.
   * @return Projection operation with fields and rounded stats.
   */
  private ProjectionOperation createProjectionOperation(
      List<String> metrics, List<String> stats, LocalDate startDate, LocalDate endDate) {
    ProjectionOperation projectOperation =
        Aggregation.project()
            .and("_id")
            .as("sensorId")
            .and("timestamp")
            .as("timestamp")
            .andExclude("_id");
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

  /**
   * Builds the complete aggregation pipeline.
   *
   * @param matchOperation Initial match operation to filter by sensorId.
   * @param dateFilterOperation Optional date filter to limit by timestamp range.
   * @param sortOperation Sort operation by timestamp in descending order.
   * @param groupOperation Grouping operation to calculate metrics.
   * @param projectOperation Projection operation to include and format specific fields.
   * @param startDate Start date for the query.
   * @param endDate End date for the query.
   * @return Aggregation pipeline with all specified operations.
   */
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
