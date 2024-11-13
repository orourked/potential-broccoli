package org.orourked.weatherapi.dto;

import jakarta.validation.constraints.NotNull;

public class WeatherSaveRequest {

  @NotNull(message = "sensorId is required")
  private String sensorId;

  @NotNull(message = "location is required")
  private String location;

  @NotNull(message = "temperature is required")
  private Double temperature;

  @NotNull(message = "humidity is required")
  private Double humidity;

  @NotNull(message = "windspeed is required")
  private Double windspeed;

  public @NotNull(message = "sensorId is required") String getSensorId() {
    return sensorId;
  }

  public @NotNull(message = "location is required") String getLocation() {
    return location;
  }

  public @NotNull(message = "temperature is required") Double getTemperature() {
    return temperature;
  }

  public @NotNull(message = "humidity is required") Double getHumidity() {
    return humidity;
  }

  public @NotNull(message = "windspeed is required") Double getWindspeed() {
    return windspeed;
  }

  public @NotNull(message = "pressure is required") Double getPressure() {
    return pressure;
  }

  public @NotNull(message = "timestamp is required") String getTimestamp() {
    return timestamp;
  }

  @NotNull(message = "pressure is required")
  private Double pressure;

  @NotNull(message = "timestamp is required")
  private String timestamp;
}
