package org.orourked.weatherapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.orourked.weatherapi.database.WeatherDatabase;
import org.orourked.weatherapi.model.WeatherData;

class WeatherServiceTest {
  @Mock
  private WeatherDatabase weatherDatabase;

  @InjectMocks
  private WeatherService weatherService;

  @BeforeEach
  void setUp(){
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAllWeatherData() {
    // Mock data
    WeatherData weatherData1 = new WeatherData();
    weatherData1.setId("1");
    weatherData1.setSensorId("sensor1");
    weatherData1.setLocation("New York");
    weatherData1.setTemperature(25.0);
    weatherData1.setHumidity(65.0);
    weatherData1.setWindspeed(17.0);
    weatherData1.setPressure(1013.0);
    weatherData1.setTimestamp(String.valueOf(LocalDateTime.now().minusHours(1)));

    WeatherData weatherData2 = new WeatherData();
    weatherData2.setId("2");
    weatherData2.setSensorId("sensor2");
    weatherData2.setLocation("London");
    weatherData2.setTemperature(15.0);
    weatherData2.setHumidity(55.0);
    weatherData2.setWindspeed(22.0);
    weatherData2.setPressure(1015.0);
    weatherData2.setTimestamp(String.valueOf(LocalDateTime.now()));
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
    weatherData1.setTimestamp(String.valueOf(LocalDateTime.now().minusHours(1)));
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
}
