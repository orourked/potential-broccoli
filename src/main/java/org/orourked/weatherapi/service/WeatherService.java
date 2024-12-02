package org.orourked.weatherapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.orourked.weatherapi.database.WeatherAggregation;
import org.orourked.weatherapi.database.WeatherDatabase;
import org.orourked.weatherapi.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WeatherService {
  private final WeatherDatabase weatherDatabase;
  private final WeatherAggregation weatherAggregation;

  @Autowired
  public WeatherService(WeatherDatabase weatherDatabase, WeatherAggregation weatherAggregation) {
    this.weatherDatabase = weatherDatabase;
    this.weatherAggregation = weatherAggregation;
  }

  private static LocalDate parseDate(String date) {
    try {
      return StringUtils.hasText(date) ? LocalDate.parse(date) : null;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
    }
  }

  public List<WeatherData> getAllWeatherData() {
    List<WeatherData> allWeatherData;
    try {
      allWeatherData = weatherDatabase.findAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Connection Problems to Database");
    }
    return allWeatherData;
  }

  public List<WeatherData> getWeatherDataByLocation(String location) {
    List<WeatherData> weatherDataByLocation;
    try {
      weatherDataByLocation = weatherDatabase.findByLocation(location);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Connection Problems to Database");
    }
    return weatherDataByLocation;
  }

  public List<Map> queryWeatherData(
      List<String> sensorIds,
      List<String> metrics,
      List<String> stats,
      String startDate,
      String endDate) {

    LocalDate start = parseDate(startDate);
    LocalDate end = parseDate(endDate);

    try {
      return weatherAggregation.queryWeatherData(sensorIds, metrics, stats, start, end);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid stat");
    }
  }

  public void saveWeatherData(WeatherData weatherData) {
    weatherDatabase.save(weatherData);
  }
}
