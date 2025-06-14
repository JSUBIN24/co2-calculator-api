#!/bin/bash

# Set ORS token (Bash syntax)
export ORS_TOKEN=5b3ce3597851110001cf62485c08d2ebd0c846c482dfd05a9835b9ed

# Build
mvn clean install

# Run the app
java -jar target/co2calculator-0.0.1-SNAPSHOT.jar \
  --start="Hamburg" \
  --end="Berlin" \
  --transportation-method=diesel-car-medium

# Wait for user input before exit
read -p "Press Enter to exit..."
