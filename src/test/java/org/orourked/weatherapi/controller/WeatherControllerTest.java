package org.orourked.weatherapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.orourked.weatherapi.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private WeatherService weatherService;

  @Test
  void testSaveWeatherData_missingWindspeed_shouldTriggerValidationError() throws Exception {
    // Create an instance of the request object with a missing "windspeed" (a required field)
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

    // Perform the POST request and verify the 400 Bad Request status and validation error message
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
}
