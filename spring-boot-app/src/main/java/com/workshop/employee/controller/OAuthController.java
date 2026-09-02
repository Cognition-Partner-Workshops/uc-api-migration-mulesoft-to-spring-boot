package com.workshop.employee.controller;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.exception.BadRequestException;
import com.workshop.employee.service.TokenService;
import org.springframework.web.bind.annotation.*;

@RestController
public class OAuthController {
    private final TokenService tokenService;

    public OAuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping(value = "/oauth/token", consumes = "application/x-www-form-urlencoded")
    public TokenResponse token(@RequestParam("grant_type") String grantType,
                               @RequestParam("client_id") String clientId,
                               @RequestParam("client_secret") String clientSecret) {
        if (!"client_credentials".equals(grantType)) {
            throw new BadRequestException("Unsupported grant type", "unsupported_grant_type");
        }
        return new TokenResponse(tokenService.issueToken(clientId, clientSecret), "Bearer",
                Math.toIntExact(tokenService.getTokenExpirySeconds()));
    }
}
