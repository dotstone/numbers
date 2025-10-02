package com.example.numbercalculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void testCalculateFancy_ReturnsFormattedResult() throws Exception {
        // Mock the RestTemplate to return predictable numbers
        when(restTemplate.getForObject(anyString(), eq(Integer.class)))
                .thenReturn(10)
                .thenReturn(20);

        mockMvc.perform(get("/api/calculate/fancy"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Fancy Calculation Results:")))
                .andExpect(content().string(containsString("Numbers: 10 and 20")))
                .andExpect(content().string(containsString("Sum: 30")))
                .andExpect(content().string(containsString("Product: 200")))
                .andExpect(content().string(containsString("Average: 15.00")));
    }

    @Test
    void testCalculateFancy_ChecksPrimeNumber() throws Exception {
        // Mock to return numbers that sum to a prime (5 + 6 = 11, which is prime)
        when(restTemplate.getForObject(anyString(), eq(Integer.class)))
                .thenReturn(5)
                .thenReturn(6);

        mockMvc.perform(get("/api/calculate/fancy"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sum: 11 (prime)")));
    }

    @Test
    void testCalculateFancy_ChecksNonPrimeNumber() throws Exception {
        // Mock to return numbers that sum to a non-prime (4 + 6 = 10, which is not prime)
        when(restTemplate.getForObject(anyString(), eq(Integer.class)))
                .thenReturn(4)
                .thenReturn(6);

        mockMvc.perform(get("/api/calculate/fancy"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sum: 10 (not prime)")));
    }

    @Test
    void testCalculateFancy_CalculatesProduct() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(Integer.class)))
                .thenReturn(7)
                .thenReturn(8);

        mockMvc.perform(get("/api/calculate/fancy"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Product: 56")));
    }

    @Test
    void testCalculateFancy_CalculatesAverage() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(Integer.class)))
                .thenReturn(15)
                .thenReturn(25);

        mockMvc.perform(get("/api/calculate/fancy"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Average: 20.00")));
    }
}
