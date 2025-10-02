# Spring Boot Microservices Demo

This project consists of two microservices:
1. **Number Generator Service** - Generates random numbers (runs on port 8082)
2. **Number Calculator Service** - Performs calculations using numbers from the generator service (runs on port 8083)

## Prerequisites
- Java 21 JDK
- Maven Wrapper is included (no need to install Maven separately)

## Building the Project

1. Build the parent project (this will build both services):
   
   **Windows:**
   ```bash
   .\mvnw.cmd clean install
   ```
   
   **Linux/Mac:**
   ```bash
   ./mvnw clean install
   ```

## Running the Services

### Option 1: Run from Command Line

1. Start the Number Generator Service:
   
   **Windows:**
   ```bash
   cd number-generator
   ..\mvnw.cmd spring-boot:run
   ```
   
   **Linux/Mac:**
   ```bash
   cd number-generator
   ../mvnw spring-boot:run
   ```

2. In a new terminal, start the Number Calculator Service:
   
   **Windows:**
   ```bash
   cd number-calculator
   ..\mvnw.cmd spring-boot:run
   ```
   
   **Linux/Mac:**
   ```bash
   cd number-calculator
   ../mvnw spring-boot:run
   ```

### Option 2: Run from IDE

1. Import the project as a Maven project in your IDE
2. Run `NumberGeneratorApplication` and `NumberCalculatorApplication` as Spring Boot applications

## Testing the Services

1. Access the Number Generator directly:
   ```
   GET http://localhost:8082/api/numbers/random
   ```
   Returns a random number between 1 and 100.

2. Access the Number Calculator's fancy calculation:
   ```
   GET http://localhost:8083/api/calculate/fancy
   ```
   This will call the Number Generator service twice to get two random numbers and return various calculations based on them.

## Architecture

- **Number Generator Service** (port 8082):
  - Simple REST endpoint that returns a random number
  
- **Number Calculator Service** (port 8083):
  - Makes HTTP requests to the Number Generator service
  - Performs various mathematical operations on the received numbers
  - Returns a formatted string with the results

## Future Improvements
- Add service discovery with Spring Cloud Netflix Eureka
- Add API Gateway with Spring Cloud Gateway
- Implement circuit breakers with Resilience4j
- Add logging and monitoring
- Containerize with Docker
