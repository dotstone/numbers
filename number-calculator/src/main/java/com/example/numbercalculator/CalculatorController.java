package com.example.numbercalculator;

import java.util.Locale;
import java.util.Objects;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * REST controller for performing calculations on numbers.
 * Retrieves random numbers from the Number Generator service and performs various calculations.
 */
@RestController
@RequestMapping("/api/calculate")
public class CalculatorController {

  private static final String GENERATOR_URL = "http://localhost:8082/api/numbers/random";
  private final RestTemplate restTemplate;

  /**
   * Constructs a new CalculatorController with the given RestTemplate.
   *
   * @param restTemplate the RestTemplate for making HTTP requests (must not be null)
   * @throws NullPointerException if restTemplate is null
   */
  public CalculatorController(RestTemplate restTemplate) {
    this.restTemplate = Objects.requireNonNull(restTemplate, "RestTemplate must not be null");
  }

  /**
   * Performs fancy calculations on two random numbers from the generator service.
   * Calculates sum, product, average, and checks if the sum is prime.
   *
   * @return formatted string with calculation results
   */
  @GetMapping("/fancy")
  public String calculateFancy() {
    // Get two random numbers from the generator service
    Integer num1Obj = null;
    Integer num2Obj = null;
    
    try {
      num1Obj = restTemplate.getForObject(GENERATOR_URL, Integer.class);
      num2Obj = restTemplate.getForObject(GENERATOR_URL, Integer.class);
    } catch (RestClientException e) {
      return "Error: Failed to retrieve random numbers from generator service: " + e.getMessage();
    }

    // Validate responses
    if (num1Obj == null || num2Obj == null) {
      return "Error: Failed to retrieve random numbers from generator service";
    }

    final int num1 = num1Obj;
    final int num2 = num2Obj;

    // Perform some fancy calculations
    int sum = num1 + num2;
    int product = num1 * num2;
    double average = (num1 + num2) / 2.0;
    String isPrime = isPrime(sum) ? "prime" : "not prime";

    try {
      return String.format(Locale.US,
          "Fancy Calculation Results:%n" +
          "Numbers: %d and %d%n" +
          "Sum: %d (%s)%n" +
          "Product: %d%n" +
          "Average: %.2f%n",
          num1, num2, sum, isPrime, product, average);
    } catch (Exception e) {
      return "Error: Failed to format calculation results: " + e.getMessage();
    }
  }

  /**
   * Checks if a number is prime.
   *
   * @param n the number to check
   * @return true if the number is prime, false otherwise
   */
  private boolean isPrime(int n) {
    if (n <= 1) {
      return false;
    }
    if (n <= 3) {
      return true;
    }
    if (n % 2 == 0 || n % 3 == 0) {
      return false;
    }
    for (int i = 5; i * i <= n; i += 6) {
      if (n % i == 0 || n % (i + 2) == 0) {
        return false;
      }
    }
    return true;
  }
}
