package com.example.numbergenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Number Generator microservice.
 * This service generates random numbers between 1 and 100.
 */
@SpringBootApplication
public class NumberGeneratorApplication {

  /**
   * Main entry point for the Spring Boot application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(NumberGeneratorApplication.class, args);
  }
}
