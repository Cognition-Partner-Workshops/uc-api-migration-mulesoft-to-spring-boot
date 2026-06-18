package com.workshop.employee.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PtoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void obtainToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "demo-client")
                        .param("client_secret", "demo-secret"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        token = json.get("access_token").asText();
    }

    @Test
    void getBalanceWithTokenReturns200() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/pto/balance")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.balance").value(120.0))
                .andExpect(jsonPath("$.used").value(40.0))
                .andExpect(jsonPath("$.total").value(160.0));
    }

    @Test
    void getBalanceWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/pto/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBalanceNotFoundReturns404() throws Exception {
        mockMvc.perform(get("/api/employee/UNKNOWN/pto/balance")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void schedulePtoWithTokenReturns200() throws Exception {
        String requestBody = """
                {
                    "startDate": "2025-12-22",
                    "endDate": "2025-12-26",
                    "hours": 40.0
                }
                """;

        mockMvc.perform(post("/api/employee/EMP001/pto/schedule")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PTO scheduled successfully"))
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(jsonPath("$.startDate").value("2025-12-22"))
                .andExpect(jsonPath("$.endDate").value("2025-12-26"))
                .andExpect(jsonPath("$.hoursScheduled").value(40.0));
    }

    @Test
    void schedulePtoWithoutTokenReturns401() throws Exception {
        String requestBody = """
                {
                    "startDate": "2025-12-22",
                    "endDate": "2025-12-26",
                    "hours": 40.0
                }
                """;

        mockMvc.perform(post("/api/employee/EMP001/pto/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
