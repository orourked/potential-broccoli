# potential-broccoli

RESTful API service that receives weather data from sensors

# Running

> [!WARNING]
> The connection string to the MongoDB cluster is omitted from this source code.

## From an IDE

The main application class is `WeatherApiApplication`, run the application from here.

## JAR

Package the source into a jar file using the Maven-generated `mvnw` script:
> [!IMPORTANT]  
> It may be necessary to give the script execute permission before running.
> ```bash
> chmod +x ./mvnw
> ```

```bash
./mvnw clean package
```

Run the packaged jar file using:

```bash
java -jar target/weather-api-1.0.0.jar
```

This will create an API endpoint at http://localhost:8080/api/weather/query. Optionally, supply a
port number to run the application other than the default 8080:

```bash
java -jar target/weather-api-1.0.0.jar --server.port=9999
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
  "metrics": ["temperature", "humidity", "pressure", "windspeed"],
  "stats": ["average", "max", "min"],
  "startDate": "2024-11-02",
  "endDate": "2024-11-08"}' http://localhost:8080/api/weather/query
```

### Using Postman

Create a `POST` request to the endpoint http://localhost:8080/api/weather/query, setting the
following JSON in the body:

```json
{
  "sensorIds": [
    "sensor4",
    "sensor1"
  ],
  "metrics": [
    "temperature",
    "humidity",
    "pressure",
    "windspeed"
  ],
  "stats": [
    "average",
    "max",
    "min"
  ],
  "startDate": "2024-11-02",
  "endDate": "2024-11-08"
}
```

## Expected Results

Results in the response should take the form of an array of JSON objects, each element representing
the results of the query for a particular sensor.
For example:

```json
[
  {
    "_id": "sensor1",
    "avgTemperature": 22.5,
    "maxTemperature": 22.5,
    "minTemperature": 22.5,
    "avgHumidity": 65.0,
    "maxHumidity": 65.0,
    "minHumidity": 65.0,
    "avgPressure": 1013.0,
    "maxPressure": 1013.0,
    "minPressure": 1013.0,
    "avgWindspeed": 14.5,
    "maxWindspeed": 14.5,
    "minWindspeed": 14.5
  },
  {
    "_id": "sensor4",
    "avgTemperature": 13.0,
    "maxTemperature": 16.0,
    "minTemperature": 10.0,
    "avgHumidity": 82.1,
    "maxHumidity": 90.0,
    "minHumidity": 77.0,
    "avgPressure": 1002.3,
    "maxPressure": 1021.2,
    "minPressure": 989.7,
    "avgWindspeed": 19.1,
    "maxWindspeed": 26.0,
    "minWindspeed": 14.0
  }
]
```