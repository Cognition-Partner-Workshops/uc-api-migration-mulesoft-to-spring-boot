package com.workshop.employee.controller;

import com.workshop.employee.service.OAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PayDateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuthService oAuthService;

    private String obtainToken() {
        return oAuthService.generateToken("demo-client", "demo-secret").getAccessToken();
    }

    @Test
    @DisplayName("GET /api/employee/{id}/next-pay-date without token returns 401")
    void noTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/next-pay-date"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/employee/{id}/next-pay-date with valid token returns pay date")
    void validTokenReturnsPayDate() throws Exception {
        String token = obtainToken();

        mockMvc.perform(get("/api/employee/EMP001/next-pay-date")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.nextPayDate").isNotEmpty())
                .andExpect(jsonPath("$.payFrequency").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/employee/{id}/next-pay-date for unknown employee returns 404")
    void unknownEmployeeReturns404() throws Exception {
        String token = obtainToken();

        mockMvc.perform(get("/api/employee/UNKNOWN999/next-pay-date")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
