package com.workshop.employee.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void obtainToken() throws Exception {
        String body = mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "demo-client")
                        .param("client_secret", "demo-secret"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(body).get("access_token").asText();
    }

    @Test
    @DisplayName("GET goals with valid token returns bare array of goal strings (payload map $.goal)")
    void validTokenReturnsGoalsArray() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/goals")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]")
                        .value("Build an Agentforce Agent using the new MCP protocol for integrations."))
                .andExpect(jsonPath("$[1]").value("Complete Spring Boot certification by Q3."));
    }

    @Test
    @DisplayName("GET goals for unknown employee returns 404 with the Mule message text")
    void unknownEmployeeReturns404WithMessage() throws Exception {
        mockMvc.perform(get("/api/employee/UNKNOWN999/goals")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("No goals found for employee UNKNOWN999"))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("GET goals without Authorization header returns 401 missing_token")
    void missingTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/goals"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Authorization header is missing or invalid"))
                .andExpect(jsonPath("$.errorCode").value("missing_token"));
    }

    @Test
    @DisplayName("GET goals with a non-Bearer scheme returns 401 missing_token")
    void nonBearerSchemeReturns401() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/goals")
                        .header(HttpHeaders.AUTHORIZATION, "Basic abc"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("missing_token"));
    }

    @Test
    @DisplayName("GET goals with an unknown token returns 401 invalid_token")
    void invalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/goals")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired access token"))
                .andExpect(jsonPath("$.errorCode").value("invalid_token"));
    }

    @Test
    @DisplayName("Unknown /api path returns APIKit-style 404 before token validation")
    void unknownApiPathReturns404WithoutToken() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    @DisplayName("POST on goals returns APIKit-style 405")
    void postOnGoalsReturns405() throws Exception {
        mockMvc.perform(post("/api/employee/EMP001/goals")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").value("Method not allowed"));
    }
}
