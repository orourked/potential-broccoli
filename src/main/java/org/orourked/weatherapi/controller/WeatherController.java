package org.orourked.weatherapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.orourked.weatherapi.dto.WeatherQueryRequest;
import org.orourked.weatherapi.model.WeatherData;
import org.orourked.weatherapi.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class defines REST API endpoints for accessing and querying weather data. Allows querying
 * based on specific filters.
 */
@RestController
@RequestMapping("api/weather")
public class WeatherController {

  @Autowired private WeatherService weatherService;
  private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Endpoint to retrieve all weather data records.
   *
   * @return a list of all WeatherData objects available in the database.
   *     <p>Example usage: curl -X GET http://localhost:8080/api/weather
   */
  @GetMapping
  public List<WeatherData> getAllWeatherData() {
    return weatherService.getAllWeatherData();
  }

  /**
   * Endpoint to retrieve weather data by a specific location.
   *
   * @param location the location to search for in the WeatherData collection.
   * @return a list of WeatherData objects that match the specified location.
   *     <p>Example usage: curl -X GET "http://localhost:8080/api/weather/location/Galway"
   */
  @GetMapping("/location/{location}")
  public List<WeatherData> getWeatherDataByLocation(@PathVariable String location) {
    return weatherService.getWeatherDataByLocation((location));
  }

  /**
   * Endpoint to query weather data based on specified criteria, logging the request details with
   * timestamp and attempts to parse the request body for logging.
   *
   * @param request a WeatherQueryRequest object containing the filter criteria.
   * @return a list of weather data matching the query parameters.
   *     <p>Example usage:
   *     <p>curl -X POST -H "Content-Type: application/json" -d '{ "sensorIds": ["sensor4",
   *     "sensor1"], "metrics": ["temperature", "humidity", "pressure", "windspeed"], "stats":
   *     ["average", "max", "min"], "startDate": "2024-11-02", "endDate": "2024-11-08"}'
   *     http://localhost:8080/api/weather/query
   */
  @PostMapping("/query")
  public List<Map> queryWeatherData(@RequestBody WeatherQueryRequest request) {
    try {
      String requestJson = objectMapper.writeValueAsString(request);
      logger.info("Received request at {} with body: {}", LocalDateTime.now(), requestJson);
    } catch (JsonProcessingException e) {
      logger.error("Failed to parse request body", e);
    }
    return weatherService.queryWeatherData(
        request.getSensorIds(),
        request.getMetrics(),
        request.getStats(),
        request.getStartDate(),
        request.getEndDate());
  }
}
