package org.orourked.weatherapi.dto;

import java.util.List;

public class WeatherQueryRequest {
  private List<String> sensorIds;
  private List<String> metrics;
  private List<String> stats;
  private String startDate;
  private String endDate;

  // Getters and setters
  public List<String> getSensorIds() {
    return sensorIds;
  }

  public WeatherQueryRequest setSensorIds(List<String> sensorIds) {
    this.sensorIds = sensorIds;
    return this;
  }

  public List<String> getMetrics() {
    return metrics;
  }

  public WeatherQueryRequest setMetrics(List<String> metrics) {
    this.metrics = metrics;
    return this;
  }

  public List<String> getStats() {
    return stats;
  }

  public WeatherQueryRequest setStats(List<String> stats) {
    this.stats = stats;
    return this;
  }

  public String getStartDate() {
    return startDate;
  }

  public WeatherQueryRequest setStartDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  public String getEndDate() {
    return endDate;
  }

  public WeatherQueryRequest setEndDate(String endDate) {
    this.endDate = endDate;
    return this;
  }
}
