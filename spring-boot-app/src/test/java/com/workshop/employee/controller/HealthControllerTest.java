package com.workshop.employee.controller;

import com.workshop.employee.service.HealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthService healthService;

    @Test
    void healthReturns200WhenDatabaseUp() throws Exception {
        when(healthService.isDatabaseUp()).thenReturn(true);

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("connected"));
    }

    @Test
    void healthReturns503WhenDatabaseDown() throws Exception {
        when(healthService.isDatabaseUp()).thenReturn(false);

        mockMvc.perform(get("/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
