# potential-broccoli

RESTful API service that receives weather data from sensors

# Running

The following instructions are run in a **bash** shell environment, such
as [Git Bash](https://git-scm.com/downloads).

## All-in-one script

The script `startApplication.sh` will build a jar file from source and then run it. Run the
following commands to do this:

```bash
chmod +x startApplication.sh

source ./startApplication.sh
```

> [!IMPORTANT]
> The connection string to the MongoDB cluster is omitted from this source code.
> However, the previously mentioned script will set necessary environment variables that will allow
> connectivity.
>
> I know this isn't best practise, but rather for the sake of convenience.

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

Optionally, supply a port number to run the application other than the default 8080:

```bash
java -jar target/weather-api-1.0.0.jar --server.port=9999
```

### Pre-packaged JAR file

Alternatively, use the pre-packaged jar file under
releases [weather-api-1.0.0.jar](https://github.com/orourked/potential-broccoli/releases/tag/v1.0.0).

# The Endpoint

Whichever approach is used to run the application, when it is running, the API endpoint
will be http://localhost:8080/api/weather/query, or if a custom port is specified, http://localhost:
{SPECIFIED_PORT_NUMBER}/api/weather/query

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

# Adding New Metric Data

Using curl commands like the following (or by using POST queries in Postman) will add new metric
data:

```bash
curl -X POST http://localhost:8080/api/weather/save -H "Content-Type: application/json" -d '{
           "sensorId": "sensor10",
           "location": "Galway",
           "temperature": 18.5,
           "humidity": 60,
           "windspeed": 10,
           "pressure": 1015,
           "timestamp": "2024-11-13T10:00:00"
         }'
```

A successful request should give a 201 Created response and the application sgould give the message
`Weather data saved successfully`.

The fields shown above are all configured to be required, so if any are omitted, the response type
would be 400 (Bad Request) and the application should give a message such as
`Validation failed: timestamp is required`.

## Expected Query Results

Results in the query response should take the form of an array of JSON objects, each element
representing
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