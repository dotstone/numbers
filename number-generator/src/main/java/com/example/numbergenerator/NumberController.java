package com.example.numbergenerator;

import java.util.Random;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for generating random numbers.
 * Provides endpoints for retrieving random numbers.
 */
@RestController
@RequestMapping("/api/numbers")
public class NumberController {

  private final Random random = new Random();

  /**
   * Generates and returns a random number between 1 and 100 (inclusive).
   *
   * @return a random integer between 1 and 100
   */
  @GetMapping("/random")
  public int getRandomNumber() {
    return random.nextInt(100) + 1;
  }
}
