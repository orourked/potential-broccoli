package org.orourked.weatherapi.database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
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

    // Convert startDate and endDate to proper MongoDB date format if provided
    LocalDate finalStartDate = startDate != null ? startDate : LocalDate.now().minusDays(1);
    LocalDate finalEndDate = endDate != null ? endDate : LocalDate.now();

    // Create the list of aggregation stages
    List<AggregationOperation> aggregationOperations = new ArrayList<>();

    // Add match stage for date range
    aggregationOperations.add(
        Aggregation.match(Criteria.where("timestamp").gte(finalStartDate).lte(finalEndDate)));

    // Add match stage for sensor IDs, if specified
    if (sensorIds != null && !sensorIds.isEmpty()) {
      aggregationOperations.add(Aggregation.match(Criteria.where("sensorId").in(sensorIds)));
    }

    // Start building the group stage
    GroupOperation groupOperation = Aggregation.group("sensorId");

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

    // Add the group operation to the aggregation stages
    aggregationOperations.add(groupOperation);

    // Build the final aggregation pipeline
    Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

    // Execute aggregation and collect results
    AggregationResults<Map> results;
    results = mongoTemplate.aggregate(aggregation, "weatherData", Map.class);
    return results.getMappedResults();
  }
}
