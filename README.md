# Spring Boot Microservices Demo

This project consists of two microservices with comprehensive CI/CD and code quality enforcement:
1. **Number Generator Service** - Generates random numbers (runs on port 8082)
2. **Number Calculator Service** - Performs calculations using numbers from the generator service (runs on port 8083)

## Prerequisites
- Java 21 JDK
- Maven Wrapper is included (no need to install Maven separately)
- Jenkins (for CI/CD pipeline) - see [JENKINS_PLUGINS.md](JENKINS_PLUGINS.md) for required plugins

## Code Quality Standards

This project enforces strict code quality standards through automated checks:

- ✅ **Code Style**: Checkstyle (Google Java Style Guide)
- ✅ **Code Quality**: PMD (code quality and duplicate detection)
- ✅ **Bug Detection**: SpotBugs (static analysis)
- ✅ **Code Coverage**: JaCoCo (≥80% line, ≥70% branch)
- ✅ **Security**: OWASP Dependency Check (no high-severity vulnerabilities)

See [CODE_QUALITY.md](CODE_QUALITY.md) for detailed guidelines.

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

## CI/CD Pipeline

The project includes a comprehensive Jenkins pipeline with:

### Pipeline Stages
1. **Version** - Semantic versioning based on conventional commits
2. **Checkout** - Source code retrieval
3. **Build** - Maven compilation
4. **Code Quality Analysis** - Parallel execution of:
   - Checkstyle (code style)
   - PMD (code quality)
   - SpotBugs (bug detection)
5. **Test & Coverage** - Unit tests with JaCoCo coverage
6. **Security Scan** - OWASP dependency vulnerability check
7. **Package** - JAR artifact creation
8. **Archive** - Artifact storage
9. **Quality Gate** - Enforce quality thresholds

### Quality Gates
- Build fails if code coverage < 80% (line) or < 70% (branch)
- Build becomes UNSTABLE if code quality issues are detected
- Build fails if high-severity security vulnerabilities found (CVSS ≥7)

### Running Quality Checks Locally

```bash
# Run all quality checks
./mvnw clean verify

# Run specific checks
./mvnw checkstyle:check      # Code style
./mvnw pmd:check              # Code quality
./mvnw spotbugs:check         # Bug detection
./mvnw test jacoco:report     # Tests with coverage
./mvnw dependency-check:check # Security scan
```

## Project Structure

```
.
├── number-generator/          # Random number generation service
├── number-calculator/         # Calculation service
├── Jenkinsfile               # CI/CD pipeline definition
├── pom.xml                   # Parent POM with quality plugins
├── CODE_QUALITY.md           # Code quality guidelines
├── JENKINS_PLUGINS.md        # Required Jenkins plugins
├── dependency-check-suppressions.xml
└── checkstyle-suppressions.xml
```

## Future Improvements
- Add service discovery with Spring Cloud Netflix Eureka
- Add API Gateway with Spring Cloud Gateway
- Implement circuit breakers with Resilience4j
- Add distributed tracing with Zipkin/Jaeger
- Integrate SonarQube for advanced code analysis
- Add performance testing with JMeter
- Containerize with Docker and Kubernetes
- Implement blue-green deployments
