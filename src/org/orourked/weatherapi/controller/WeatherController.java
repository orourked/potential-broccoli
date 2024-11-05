package org.orourked.weatherapi.controller;

import java.util.List;
import org.orourked.weatherapi.model.WeatherData;
import org.orourked.weatherapi.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/weather")
public class WeatherController {

  @Autowired
  private WeatherService weatherService;

  @GetMapping
  public List<WeatherData> getAllWeatherData() {
    return weatherService.getAllWeatherData();
  }

  @GetMapping("/location/{location}")
  public List<WeatherData> getWeatherDataByLocation(@PathVariable String location) {
    return weatherService.getWeatherDataByLocation((location));
  }
}
