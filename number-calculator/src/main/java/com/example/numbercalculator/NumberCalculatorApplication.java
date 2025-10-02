package com.example.numbercalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@SpringBootApplication
public class NumberCalculatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(NumberCalculatorApplication.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@RestController
@RequestMapping("/api/calculate")
class CalculatorController {
    private final RestTemplate restTemplate;
    private static final String GENERATOR_URL = "http://localhost:8082/api/numbers/random";
    
    public CalculatorController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @GetMapping("/fancy")
    public String calculateFancy() {
        // Get two random numbers from the generator service
        int num1 = restTemplate.getForObject(GENERATOR_URL, Integer.class);
        int num2 = restTemplate.getForObject(GENERATOR_URL, Integer.class);
        
        // Perform some fancy calculations
        int sum = num1 + num2;
        int product = num1 * num2;
        double average = (num1 + num2) / 2.0;
        String isPrime = isPrime(sum) ? "prime" : "not prime";
        
        return String.format(Locale.US, """
            Fancy Calculation Results:
            Numbers: %d and %d
            Sum: %d (%s)
            Product: %d
            Average: %.2f
            """, num1, num2, sum, isPrime, product, average);
    }
    
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}
