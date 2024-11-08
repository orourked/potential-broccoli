package org.orourked.weatherapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "weatherData")
public class WeatherData {
  @Id private String id;
  private String sensorId;
  private String location;
  private double temperature;
  private double humidity;
  private double windspeed;
  private double pressure;
  private String timestamp;

  // Getters
  public String getId() {
    return id;
  }

  public String getSensorId() {
    return sensorId;
  }

  public String getLocation() {
    return location;
  }

  public double getTemperature() {
    return temperature;
  }

  public double getHumidity() {
    return humidity;
  }

  public double getWindspeed() {
    return windspeed;
  }

  public double getPressure() {
    return pressure;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public WeatherData setId(String id) {
    this.id = id;
    return this;
  }

  public WeatherData setSensorId(String sensorId) {
    this.sensorId = sensorId;
    return this;
  }

  public WeatherData setLocation(String location) {
    this.location = location;
    return this;
  }

  public WeatherData setTemperature(double temperature) {
    this.temperature = temperature;
    return this;
  }

  public WeatherData setHumidity(double humidity) {
    this.humidity = humidity;
    return this;
  }

  public WeatherData setWindspeed(double windspeed) {
    this.windspeed = windspeed;
    return this;
  }

  public WeatherData setPressure(double pressure) {
    this.pressure = pressure;
    return this;
  }

  public WeatherData setTimestamp(String timestamp) {
    this.timestamp = timestamp;
    return this;
  }
}
