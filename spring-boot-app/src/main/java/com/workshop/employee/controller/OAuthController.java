package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.service.OAuthService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Mirrors the MuleSoft oauth-token-flow.
 * Accepts form-urlencoded client credentials and returns a bearer token.
 */
@RestController
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret) {

        Optional<TokenResponse> tokenOpt = oAuthService.generateToken(clientId, clientSecret);
        if (tokenOpt.isEmpty()) {
            ErrorResponse error = new ErrorResponse("Client authentication failed", "INVALID_CLIENT");
            return ResponseEntity.status(401).body(error);
        }
        return ResponseEntity.ok(tokenOpt.get());
    }
}
