package org.orourked.weatherapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orourked.weatherapi.database.WeatherAggregation;
import org.orourked.weatherapi.database.WeatherDatabase;
import org.orourked.weatherapi.model.WeatherData;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class WeatherServiceTest {
  @Mock private WeatherDatabase weatherDatabase;
  @Mock private WeatherAggregation weatherAggregation;

  @InjectMocks private WeatherService weatherService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAllWeatherData() {
    // Mock data
    WeatherData weatherData1 =
        new WeatherData()
            .setId("1")
            .setSensorId("sensor1")
            .setLocation("New York")
            .setTemperature(25.0)
            .setHumidity(65.0)
            .setWindspeed(17.0)
            .setPressure(1013.0)
            .setTimestamp(LocalDateTime.now().minusHours(1));

    WeatherData weatherData2 =
        new WeatherData()
            .setId("2")
            .setSensorId("sensor2")
            .setLocation("London")
            .setTemperature(15.0)
            .setHumidity(55.0)
            .setWindspeed(22.0)
            .setPressure(1015.0)
            .setTimestamp(LocalDateTime.now());
    List<WeatherData> mockData = Arrays.asList(weatherData1, weatherData2);

    // Stub the repository's behavior
    when(weatherDatabase.findAll()).thenReturn(mockData);

    // Call the service
    List<WeatherData> result = weatherService.getAllWeatherData();

    // Verify and assert
    assertEquals(2, result.size());
    verify(weatherDatabase, times(1)).findAll();
  }

  @Test
  void testGetWeatherDataByLocation() {
    // Mock data
    WeatherData weatherData1 = new WeatherData();
    weatherData1.setId("1");
    weatherData1.setSensorId("sensor1");
    weatherData1.setLocation("New York");
    weatherData1.setTemperature(25.0);
    weatherData1.setHumidity(65.0);
    weatherData1.setWindspeed(17.0);
    weatherData1.setPressure(1013.0);
    weatherData1.setTimestamp(LocalDateTime.now().minusHours(1));
    List<WeatherData> mockData = List.of(weatherData1);

    // Stub the repository's behavior
    when(weatherDatabase.findByLocation("New York")).thenReturn(mockData);

    // Call the service
    List<WeatherData> result = weatherService.getWeatherDataByLocation("New York");

    // Verify and assert
    assertEquals(1, result.size());
    assertEquals("New York", result.get(0).getLocation());
    verify(weatherDatabase, times(1)).findByLocation("New York");
  }

  @Test
  void testGetAllWeatherDataThrowsException() {
    // Stub the repository's behavior
    when(weatherDatabase.findAll()).thenThrow(new RuntimeException("MongoDB connection error"));

    // Call the service
    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> weatherService.getAllWeatherData());

    // Verify and assert
    assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
  }

  @Test
  void testGetAllWeatherByLocationThrowsException() {
    // Stub the repository's behavior
    when(weatherDatabase.findByLocation(anyString()))
        .thenThrow(new RuntimeException("MongoDB connection error"));

    // Call the service
    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> weatherService.getWeatherDataByLocation("New York"));

    // Verify and assert
    assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
  }

  @Test
  void testBadDateException() {
    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> weatherService.queryWeatherData(List.of(), List.of(), List.of(), "junk", "junk"));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
  }

  @Test
  void testBadStatException() {
    when(weatherAggregation.queryWeatherData(any(), any(), any(), any(), any()))
        .thenThrow(new IllegalArgumentException("Unknown Stat"));

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () ->
                weatherService.queryWeatherData(
                    List.of("sensor1"),
                    List.of("temperature"),
                    List.of("invalid_stat"),
                    null,
                    null));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
  }
}
