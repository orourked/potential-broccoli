package org.orourked.weatherapi.database;

import java.util.List;
import org.orourked.weatherapi.model.WeatherData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and performing CRUD operations on weather data in MongoDB.
 * Extends Spring Data's MongoRepository to allow for custom queries. Provides methods for
 * retrieving weather data records by location or by ID.
 */
@Repository
public interface WeatherDatabase extends MongoRepository<WeatherData, String> {
  /**
   * Finds a list of WeatherData records that match the specified location.
   *
   * @param location the location to search for in the WeatherData collection
   * @return a list of WeatherData objects associated with the specified location
   */
  List<WeatherData> findByLocation(String location);
}
