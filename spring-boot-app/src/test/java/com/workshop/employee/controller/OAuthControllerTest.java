package com.workshop.employee.controller;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.exception.InvalidClientException;
import com.workshop.employee.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @Test
    @DisplayName("POST /oauth/token with valid credentials returns 200 with token")
    void validCredentialsReturnToken() throws Exception {
        when(tokenService.authenticate("demo-client", "demo-secret"))
                .thenReturn(new TokenResponse("test-token-123", "Bearer", 3600));

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "demo-client")
                        .param("client_secret", "demo-secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("test-token-123"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(3600));
    }

    @Test
    @DisplayName("POST /oauth/token with invalid credentials returns 401")
    void invalidCredentialsReturn401() throws Exception {
        when(tokenService.authenticate("bad-client", "bad-secret"))
                .thenThrow(new InvalidClientException("Client authentication failed"));

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "bad-client")
                        .param("client_secret", "bad-secret"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Client authentication failed"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_CLIENT"));
    }

    @Test
    @DisplayName("POST /oauth/token with Basic Auth returns 200")
    void basicAuthReturnToken() throws Exception {
        when(tokenService.authenticate("demo-client", "demo-secret"))
                .thenReturn(new TokenResponse("test-token-basic", "Bearer", 3600));

        String encoded = java.util.Base64.getEncoder()
                .encodeToString("demo-client:demo-secret".getBytes());

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("Authorization", "Basic " + encoded))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("test-token-basic"))
                .andExpect(jsonPath("$.token_type").value("Bearer"));
    }

    @Test
    @DisplayName("GET /api/** without token returns 401")
    void apiEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/goals"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errorCode").value("MISSING_TOKEN"));
    }
}
