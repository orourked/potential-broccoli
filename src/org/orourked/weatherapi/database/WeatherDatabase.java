package org.orourked.weatherapi.database;

import java.util.List;
import org.orourked.weatherapi.model.WeatherData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WeatherDatabase {
  List<WeatherData> findByLocation(String location);
}
