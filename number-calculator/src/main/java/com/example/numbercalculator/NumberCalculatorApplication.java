package com.example.numbercalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main application class for the Number Calculator microservice.
 * This service performs calculations using numbers from the Number Generator service.
 */
@SpringBootApplication
public class NumberCalculatorApplication {

  /**
   * Main entry point for the Spring Boot application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(NumberCalculatorApplication.class, args);
  }

  /**
   * Creates a RestTemplate bean for making HTTP requests.
   *
   * @return configured RestTemplate instance
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
