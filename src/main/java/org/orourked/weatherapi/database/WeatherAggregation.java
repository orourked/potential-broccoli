package org.orourked.weatherapi.database;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherAggregation {
  @Autowired
  private MongoTemplate mongoTemplate;

  public List<Map> queryWeatherData(List<String> sensorIds, List<String> metrics, List<String> stats, LocalDate startDate, LocalDate endDate) {

    // Convert startDate and endDate to proper MongoDB date format if provided
    LocalDate finalStartDate = startDate != null ? startDate : LocalDate.now().minusDays(1);
    LocalDate finalEndDate = endDate != null ? endDate : LocalDate.now();

    // Add match stage to filter by date range and sensor IDs
    Aggregation aggregation = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("timestamp").gte(finalStartDate).lte(finalEndDate)),
        sensorIds != null ? Aggregation.match(Criteria.where("sensorId").in(sensorIds)) : null,

        // Add aggregation stages for each metric and requested statistics
        Aggregation.group("sensorId") // Group by sensor ID
            .avg("temperature").as("avgTemperature")
            .min("temperature").as("minTemperature")
            .max("temperature").as("maxTemperature")
            .sum("temperature").as("sumTemperature")
        // Repeat for other metrics as needed (humidity, etc.)
    );

    // Execute aggregation and collect results
    AggregationResults<Map> results;
    results = mongoTemplate.aggregate(aggregation, "weatherData", Map.class);
    return results.getMappedResults();
  }
}
