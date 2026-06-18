package com.workshop.employee.controller;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.service.TokenService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

    private final TokenService tokenService;

    public OAuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * POST /oauth/token — OAuth2 client-credentials token generation.
     * Supports both form-encoded body credentials and Basic Auth header,
     * matching the MuleSoft oauth-token-flow choice router behavior.
     */
    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<TokenResponse> getToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "grant_type", defaultValue = "client_credentials") String grantType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret) {

        log.info("OAuth token request received");

        // Basic Auth takes precedence, matching MuleSoft choice router
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String encoded = authHeader.substring(6);
            String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", 2);
            clientId = parts[0];
            clientSecret = parts.length > 1 ? parts[1] : "";
        }

        TokenResponse tokenResponse = tokenService.authenticate(clientId, clientSecret);
        return ResponseEntity.ok(tokenResponse);
    }
}
