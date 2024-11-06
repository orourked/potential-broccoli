package org.orourked.weatherapi.service;

import java.util.List;
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
}
