package org.orourked.weatherapi.database;

import java.util.List;
import org.orourked.weatherapi.model.WeatherData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherDatabase extends MongoRepository<WeatherData, String> {
  List<WeatherData> findByLocation(String location);
  //List<WeatherData> findAll();
}
