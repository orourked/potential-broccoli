#!/bin/bash

export MONGODB_USERNAME="darrenorourke"
export MONGODB_PASSWORD="S9WMgFcrZnc2Ixf0"
export MONGODB_APPLICATION="node-rest-shop"

# Create the jar file at target/weather-api-1.0.0.jar
./mvnw clean package

# Run the jar file
java -jar target/weather-api-1.0.0.jar