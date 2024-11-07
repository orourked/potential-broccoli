package org.orourked.weatherapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.orourked.weatherapi.database.WeatherAggregation;
import org.orourked.weatherapi.database.WeatherDatabase;
import org.orourked.weatherapi.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
  @Autowired
  private WeatherDatabase weatherDatabase;

  public List<WeatherData> getAllWeatherData() {
    return weatherDatabase.findAll();
  }

  public List<WeatherData> getWeatherDataByLocation(String location) {
    return weatherDatabase.findByLocation(location);
  }


  @Autowired
  private WeatherAggregation weatherAggregation;

  public List<Map> queryWeatherData(List<String> sensorIds, List<String> metrics, List<String> stats, String startDate, String endDate) {

    LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
    LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

    return weatherAggregation.queryWeatherData(sensorIds, metrics, stats, start, end);
  }
}
