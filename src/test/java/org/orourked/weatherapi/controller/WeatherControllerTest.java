package org.orourked.weatherapi.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.orourked.weatherapi.database.WeatherAggregation;
import org.orourked.weatherapi.database.WeatherDatabase;
import org.orourked.weatherapi.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WeatherController.class)
@Import(WeatherControllerTest.TestConfig.class)
class WeatherControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private WeatherDatabase mockWeatherDatabase;

  @Autowired private WeatherAggregation mockWeatherAggregation;

  @BeforeEach
  void setupMocks() {
    // Mock the behavior for saving weather data (if applicable for validation)
    when(mockWeatherDatabase.findAll()).thenReturn(Collections.emptyList());
  }

  @Test
  void testSaveWeatherData_missingWindspeed_shouldTriggerValidationError() throws Exception {
    String requestBody =
        """
       {
          "sensorId": "sensor5",
          "location": "Galway",
          "temperature": 9.0,
          "humidity": 74.0,
          "pressure": 1036.0
       }
      """;

    mockMvc
        .perform(post("/api/weather/save").contentType("application/json").content(requestBody))
        .andExpect(status().isBadRequest()) // Expecting 400 Bad Request
        .andExpect(jsonPath("$.message").value("windspeed is required"));
  }

  @Test
  void testSaveWeatherData_validRequest_shouldNotTriggerValidationError() throws Exception {
    String requestBody =
        """
       {
          "sensorId": "sensor5",
          "location": "Galway",
          "temperature": 9.0,
          "humidity": 74.0,
          "pressure": 1036.0,
          "windspeed": 21.0
       }
      """;

    mockMvc
        .perform(post("/api/weather/save").contentType("application/json").content(requestBody))
        .andExpect(status().isCreated()) // Expecting 201
        .andExpect(jsonPath("$.message").value("Weather data saved successfully"));
  }

  static class TestConfig {

    @Bean
    public WeatherService weatherService(
        WeatherDatabase weatherDatabase, WeatherAggregation weatherAggregation) {
      return new WeatherService(weatherDatabase, weatherAggregation);
    }

    @Bean
    public WeatherDatabase weatherDatabase() {
      return mock(WeatherDatabase.class);
    }

    @Bean
    public WeatherAggregation weatherAggregation() {
      return mock(WeatherAggregation.class);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
      return mock(MongoTemplate.class);
    }
  }
}
