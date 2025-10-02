package com.example.numbergenerator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NumberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetRandomNumber_ReturnsNumber() throws Exception {
        mockMvc.perform(get("/api/numbers/random"))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("\\d+")));
    }

    @Test
    void testGetRandomNumber_ReturnsBetween1And100() throws Exception {
        // Test multiple times to ensure randomness stays within bounds
        for (int i = 0; i < 10; i++) {
            String result = mockMvc.perform(get("/api/numbers/random"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            int number = Integer.parseInt(result);
            assert number >= 1 && number <= 100 : "Number should be between 1 and 100, got: " + number;
        }
    }

    @Test
    void testGetRandomNumber_ReturnsInteger() throws Exception {
        String result = mockMvc.perform(get("/api/numbers/random"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Should not throw NumberFormatException
        Integer.parseInt(result);
    }
}
