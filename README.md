# potential-broccoli
RESTful API service that receives weather data from sensors

# Running
> [!WARNING]
> Note the connection string to the MongoDB cluster is omitted from this source code.
## From an IDE
The main application class is `WeatherApiApplication`, run the application from here.

## JAR
Package the source into a jar file using:
```bash
./mvnw clean package
```
Run the packaged jar file using:
```bash
java -jar target/weather-api-1.0.0.jar
```

# Querying
## Accepted fields

| Key       | Type           | Notes                                                             |
|-----------|----------------|-------------------------------------------------------------------|
| sensorIds | array (String) | Takes the form "sensor1", "sensor2", etc.                         |
| metrics   | array (String) | Options are "temperature", "humidity", "windspeed" and "pressure" |
| stats     | array (String) | Options are "average", "max", "min", "sum"                        |
| startDate | String         | Use the format "YYYY-MM-DD"                                       |
| endDate   | String         | Use the format "YYYY-MM-DD"                                       |

## Examples
### Using a curl command
```bash
curl -X POST -H "Content-Type: application/json" -d '{
  "sensorIds": ["sensor4", "sensor1"],
  "metrics": ["temperature", "humidity", "pressure"],
  "stats": ["average", "max", "min"],
  "startDate": "2024-11-02",
  "endDate": "2024-11-08"}' http://localhost:8080/api/weather/query
```
### Using Postman
Create a `POST` request to the endpoint http://localhost:8080/api/weather/query, setting the following JSON in the body: 
```json
{
  "sensorIds": ["sensor4", "sensor1"],
  "metrics": ["temperature", "humidity", "pressure"],
  "stats": ["average", "max", "min"],
  "startDate": "2024-11-02",
  "endDate": "2024-11-08"
}
```