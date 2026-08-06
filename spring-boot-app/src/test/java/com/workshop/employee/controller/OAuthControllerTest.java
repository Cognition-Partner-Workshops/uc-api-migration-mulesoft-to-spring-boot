package com.workshop.employee.controller;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.exception.UnauthorizedException;
import com.workshop.employee.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OAuthController.class)
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService tokenService;

    @Test
    void validCredentialsReturnToken() throws Exception {
        when(tokenService.issueToken("client_credentials", "demo-client", "demo-secret"))
                .thenReturn(new TokenResponse("abc-123", "Bearer", 3600));

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "demo-client")
                        .param("client_secret", "demo-secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("abc-123"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(3600));
    }

    @Test
    void invalidCredentialsReturn401() throws Exception {
        when(tokenService.issueToken("client_credentials", "bad-client", "bad-secret"))
                .thenThrow(new UnauthorizedException("Client authentication failed", "invalid_client"));

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "bad-client")
                        .param("client_secret", "bad-secret"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Client authentication failed"))
                .andExpect(jsonPath("$.errorCode").value("invalid_client"));
    }
}
