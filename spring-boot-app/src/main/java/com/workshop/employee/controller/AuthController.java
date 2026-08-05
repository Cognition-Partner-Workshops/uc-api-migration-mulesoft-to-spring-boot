package com.workshop.employee.controller;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping(value = "/oauth/token", consumes = "application/x-www-form-urlencoded",
                 produces = "application/json")
    public TokenResponse token(@RequestParam(value = "grant_type", required = false) String grantType,
                               @RequestParam(value = "client_id", required = false) String clientId,
        @RequestParam(value = "client_secret", required = false) String clientSecret) {
        return auth.issueToken(grantType, clientId, clientSecret);
    }
}
