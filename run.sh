#!/bin/bash

# ==============================================
# CO₂ Emissions Calculator - Cross-platform Runner (Linux/macOS/Git Bash)
# ==============================================

# Exit script on any error
set -e

# Function: Print banner
function banner() {
  echo "🌍 --------------------------------------------"
  echo "   CO₂ Emissions Calculator - Java Spring Boot"
  echo "-------------------------------------------- 🌍"
}

# Function: Validate ORS_TOKEN
function validate_token() {
  if [ -z "$ORS_TOKEN" ]; then
    echo "❌ ORS_TOKEN environment variable is not set."
    echo "Please set it before running:"
    echo ""
    echo "  export ORS_TOKEN=your-api-key"
    echo ""
    exit 1
  fi
}

# Function: Build the project
function build_project() {
  echo "🔨 Building project with Maven..."
  mvn clean package
  echo "✅ Build complete."
}

# Function: Run the application
function run_app() {
  echo "🚀 Running CO₂ Calculator..."
  java -jar target/co2calculator-0.0.1-SNAPSHOT.jar "$@"
}

# Function: Wait for exit (optional)
function wait_for_exit() {
  echo ""
  read -p "🔚 Press Enter to exit..."
}

# Main
banner
validate_token
build_project
run_app "$@"
wait_for_exit
