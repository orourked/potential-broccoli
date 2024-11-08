# potential-broccoli

RESTful API service that receives weather data from sensors

# Running

> [!WARNING]
> The connection string to the MongoDB cluster is omitted from this source code.

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
  "metrics": ["temperature", "humidity", "pressure"],
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
    "pressure"
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
    "_id": "sensor4",
    "avgtemperature": 13.0,
    "maxtemperature": 16,
    "mintemperature": 10,
    "avghumidity": 82.1,
    "maxhumidity": 90,
    "minhumidity": 77,
    "avgpressure": 1002.3,
    "maxpressure": 1021.2,
    "minpressure": 989.7
  },
  {
    "_id": "sensor1",
    "avgtemperature": 22.5,
    "maxtemperature": 22.5,
    "mintemperature": 22.5,
    "avghumidity": 65.0,
    "maxhumidity": 65,
    "minhumidity": 65,
    "avgpressure": 1013.0,
    "maxpressure": 1013,
    "minpressure": 1013
  }
]
```