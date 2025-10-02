package com.example.numbergenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Random;

@SpringBootApplication
public class NumberGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(NumberGeneratorApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/numbers")
class NumberController {
    private final Random random = new Random();
    
    @GetMapping("/random")
    public int getRandomNumber() {
        return random.nextInt(100) + 1; // Returns random number between 1 and 100
    }
}
