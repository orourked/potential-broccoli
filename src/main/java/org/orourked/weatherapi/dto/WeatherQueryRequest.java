package org.orourked.weatherapi.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO (Data Transfer Object) that encapsulates the query parameters for querying weather data. This
 * object is used to pass query filters, including sensor IDs, metrics, statistics, and date range.
 */
public class WeatherQueryRequest {

  // List of sensor IDs to filter the weather data.
  @NotEmpty(message = "At least one sensorId is required")
  private List<String> sensorIds;

  // List of metrics (e.g., temperature, humidity) to include in the query.
  @NotEmpty(message = "At least one metric is required")
  private List<String> metrics;

  // List of statistics (e.g., average, max, min) to calculate for the selected metrics.
  private List<String> stats;

  // Start date for filtering weather data (in string format, typically "yyyy-MM-dd").
  private String startDate;

  // End date for filtering weather data (in string format, typically "yyyy-MM-dd").
  private String endDate;

  // Getter for the list of sensor IDs.
  public List<String> getSensorIds() {
    return sensorIds;
  }

  /**
   * Sets the sensor IDs to filter weather data.
   *
   * @param sensorIds list of sensor IDs to filter by
   * @return the current instance of WeatherQueryRequest (for method chaining)
   */
  public WeatherQueryRequest setSensorIds(List<String> sensorIds) {
    this.sensorIds = sensorIds;
    return this;
  }

  // Getter for the list of metrics.
  public List<String> getMetrics() {
    return metrics;
  }

  /**
   * Sets the metrics to include in the query.
   *
   * @param metrics list of metrics to filter by
   * @return the current instance of WeatherQueryRequest (for method chaining)
   */
  public WeatherQueryRequest setMetrics(List<String> metrics) {
    this.metrics = metrics;
    return this;
  }

  // Getter for the list of statistics.
  public List<String> getStats() {
    return stats;
  }

  /**
   * Sets the statistics to calculate for the selected metrics.
   *
   * @param stats list of statistics (e.g., average, max, min) to compute
   * @return the current instance of WeatherQueryRequest (for method chaining)
   */
  public WeatherQueryRequest setStats(List<String> stats) {
    this.stats = stats;
    return this;
  }

  // Getter for the start date.
  public String getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date for the query.
   *
   * @param startDate the start date in "yyyy-MM-dd" format
   * @return the current instance of WeatherQueryRequest (for method chaining)
   */
  public WeatherQueryRequest setStartDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  // Getter for the end date.
  public String getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date for the query.
   *
   * @param endDate the end date in "yyyy-MM-dd" format
   * @return the current instance of WeatherQueryRequest (for method chaining)
   */
  public WeatherQueryRequest setEndDate(String endDate) {
    this.endDate = endDate;
    return this;
  }
}
