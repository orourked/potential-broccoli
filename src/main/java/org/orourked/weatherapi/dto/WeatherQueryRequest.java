package org.orourked.weatherapi.dto;

import java.util.List;

public class WeatherQueryRequest {
  private List<String> sensorIds;
  private List<String> metrics;
  private List<String> stats;
  private String startDate;
  private String endDate;

  // Getters and setters
  public List<String> getSensorIds() { return sensorIds; }
  public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }

  public List<String> getMetrics() { return metrics; }
  public void setMetrics(List<String> metrics) { this.metrics = metrics; }

  public List<String> getStats() { return stats; }
  public void setStats(List<String> stats) { this.stats = stats; }

  public String getStartDate() { return startDate; }
  public void setStartDate(String startDate) { this.startDate = startDate; }

  public String getEndDate() { return endDate; }
  public void setEndDate(String endDate) { this.endDate = endDate; }
}
