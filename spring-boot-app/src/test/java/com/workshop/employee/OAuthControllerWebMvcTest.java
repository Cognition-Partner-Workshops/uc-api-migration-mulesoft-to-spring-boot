package com.workshop.employee;

import com.workshop.employee.controller.OAuthController;
import com.workshop.employee.exception.UnauthorizedException;
import com.workshop.employee.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OAuthController.class)
class OAuthControllerWebMvcTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    TokenService tokenService;

    @Test
    void validTokenRequestReturnsToken() throws Exception {
        when(tokenService.issueToken("demo-client", "demo-secret")).thenReturn("token");
        when(tokenService.getTokenExpirySeconds()).thenReturn(3600L);

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "demo-client")
                        .param("client_secret", "demo-secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("token"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(3600));
    }

    @Test
    void invalidCredentialsReturnUnauthorized() throws Exception {
        when(tokenService.issueToken("bad", "bad"))
                .thenThrow(new UnauthorizedException("Invalid client credentials", "INVALID_CLIENT"));

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "bad")
                        .param("client_secret", "bad"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_CLIENT"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void unsupportedGrantTypeReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "password")
                        .param("client_id", "demo-client")
                        .param("client_secret", "demo-secret"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("unsupported_grant_type"));
    }
}
