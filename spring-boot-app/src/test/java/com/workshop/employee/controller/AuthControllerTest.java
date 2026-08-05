package com.workshop.employee.controller;

import com.workshop.employee.exception.GlobalExceptionHandler;
import com.workshop.employee.exception.UnauthorizedException;
import com.workshop.employee.service.AuthService;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean AuthService service;

    @Test
    void invalidCredentialsReturnUnauthorizedErrorShape() throws Exception {
        when(service.issueToken(any(), any(), any()))
            .thenThrow(new UnauthorizedException("Client authentication failed"));
        mvc.perform(post("/oauth/token").contentType("application/x-www-form-urlencoded")
                .param("grant_type", "client_credentials").param("client_id", "bad")
                .param("client_secret", "bad"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.message").value("Client authentication failed"))
            .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
    }
}
