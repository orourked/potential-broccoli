package org.orourked.weatherapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.orourked.weatherapi.dto.WeatherQueryRequest;
import org.orourked.weatherapi.dto.WeatherSaveRequest;
import org.orourked.weatherapi.model.WeatherData;
import org.orourked.weatherapi.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

  /**
   * Endpoint to save new weather metric data to the database.
   *
   * @param request weather data to save.
   * @return ResponseEntity object with descriptive string and Http response status.
   *     <p>Example usage:
   *     <p>curl -X POST http://localhost:8080/api/weather/save -H "Content-Type: application/json"
   *     -d '{ "sensorId": "sensor10", "location": "Galway", "temperature": 18.5, "humidity": 60,
   *     "windspeed": 10, "pressure": 1015, "timestamp": "2024-11-13T10:00:00" }'
   */
  @PostMapping("/save")
  public ResponseEntity<String> saveWeatherData(
      @Valid @RequestBody WeatherSaveRequest request, BindingResult bindingResult) {
    try {
      if (bindingResult.hasErrors()) {
        String errorMessage =
            bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        logger.error("Validation failed: " + errorMessage);
        return new ResponseEntity<>("Validation failed: " + errorMessage, HttpStatus.BAD_REQUEST);
      }
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
      LocalDateTime timestamp = LocalDateTime.parse(request.getTimestamp(), formatter);

      WeatherData weatherData = new WeatherData();
      weatherData.setSensorId(request.getSensorId());
      weatherData.setLocation(request.getLocation());
      weatherData.setTemperature(request.getTemperature());
      weatherData.setHumidity(request.getHumidity());
      weatherData.setWindspeed(request.getWindspeed());
      weatherData.setPressure(request.getPressure());
      weatherData.setTimestamp(timestamp.toString());
      weatherService.saveWeatherData(weatherData);
      String requestJson = objectMapper.writeValueAsString(request);
      logger.info("Received request at {} with body: {}", LocalDateTime.now(), requestJson);
      return ResponseEntity.status(HttpStatus.CREATED).body("Weather data saved successfully");
    } catch (Exception e) {
      logger.info("Failed to parse request body", e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to save weather data");
    }
  }
}
