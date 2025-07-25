# CO₂ Emissions Calculator
This project is a command-line Java Spring Boot application that calculates the total CO₂-equivalent emissions for a trip between two cities, based on the selected transportation method. It uses the OpenRouteService API for geocoding and distance matrix calculations and includes resilience features using Resilience.

### Features
* City-to-city distance calculation using OpenRouteService

* Supports transportation types: electric, hybrid, diesel, petrol cars, buses, and trains

* Calculates CO₂ emissions in kilograms

* Rounds result to 1 decimal place

* Includes retry, circuit breaker, and fallback via Resilience4j

* Asks user to disambiguate the city name if multiple locations match

* Rejects numeric or invalid city names.

* Handle unreachable distance on road.

* Introduce domain-specific custom Exception.

* Ensure errors and edge-cases.

* Uses environment variable for API key (ORS_TOKEN)

* Logs all operations via SLF4J

### Technologies Used
* Java 21

* Spring Boot 3.5.x

* WebClient (non-blocking HTTP)

* Resilience4j (Retry, CircuitBreaker) (High level)

* Lombok

* JUnit 5 + Mockito + AssertJ

### Maven

# Setup & Run

1. Run the application

Step 1:
Build and run with Maven:

# Set environment variable in power shell
$env:ORS_TOKEN=<"Your-Api-Key-Here">
$env:ORS_TOKEN="5b3ce3597851110001cf62485c08d2ebd0c846c482dfd05a9835b9ed"
echo $env:ORS_TOKEN

# Set environment variable in CMD
set ORS_TOKEN=<Your-Api-Key-Here>
set ORS_TOKEN=5b3ce3597851110001cf62485c08d2ebd0c846c482dfd05a9835b9ed
echo %ORS_TOKEN%

# Set environment variable in bash
export ORS_TOKEN=<Your-Api-Key-Here>
export ORS_TOKEN=5b3ce3597851110001cf62485c08d2ebd0c846c482dfd05a9835b9ed
echo $ORS_TOKEN

Step 2:
#build
mvn clean install

Step 3:
#Run the APP
java -jar target/co2calculator-0.0.1-SNAPSHOT.jar --start="Hamburg" --end="Berlin" --transportation-method diesel-car-medium

java -jar target/co2calculator-0.0.1-SNAPSHOT.jar --start "Los Angels" --end "New York" --transportation-method=diesel-car-medium

java -jar target/co2calculator-0.0.1-SNAPSHOT.jar --end "New York" --start "Los Angels" --transportation-method=electric-car-large

If multiple matches are found, you’ll be prompted to choose from the list.

### Testing
To run unit tests: mvn test

Includes:

* Unit tests for GeoService, EmissionService, DistanceService, OpenRoutGeoClient, OpenRouteMatrixClient, and co2calculator

* WebClient mock tests for API clients

* Exception fallback and failure flow tests

* Resilience4j Configuration (application.yml)
