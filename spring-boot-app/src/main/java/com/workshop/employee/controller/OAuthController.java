package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.service.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret) {

        log.info("OAuth token request for client: {}", clientId);

        return oAuthService.generateToken(clientId, clientSecret)
                .<ResponseEntity<?>>map(token -> ResponseEntity.ok(
                        new TokenResponse(token, "Bearer", oAuthService.getTokenExpirySeconds())))
                .orElseGet(() -> {
                    log.warn("Invalid client credentials: {}", clientId);
                    return ResponseEntity.status(401)
                            .body(new ErrorResponse("Client authentication failed", "invalid_client"));
                });
    }
}
