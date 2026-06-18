package com.workshop.employee.controller;

import com.workshop.employee.model.ApiClient;
import com.workshop.employee.repository.ApiClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GoalsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiClientRepository apiClientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String validToken;

    @BeforeEach
    void setUp() throws Exception {
        MvcResult result = mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "demo-client")
                        .param("client_secret", "demo-secret"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        validToken = body.get("access_token").asText();
    }

    @Test
    void noTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/goals"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validTokenReturnsGoals() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/goals")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void unknownEmployeeReturns404() throws Exception {
        mockMvc.perform(get("/api/employee/UNKNOWN999/goals")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No goals found for employee UNKNOWN999"));
    }
}
