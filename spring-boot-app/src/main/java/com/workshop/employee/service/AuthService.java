package com.workshop.employee.service;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.exception.UnauthorizedException;
import com.workshop.employee.repository.ApiClientRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final ApiClientRepository clients;
    private final TokenStore tokens;

    public AuthService(ApiClientRepository clients, TokenStore tokens) {
        this.clients = clients;
        this.tokens = tokens;
    }

    public TokenResponse issueToken(String grantType, String clientId, String clientSecret) {
        if (!"client_credentials".equals(grantType)
            || clientId == null
            || clientSecret == null
            || clients.findByClientIdAndClientSecret(clientId, clientSecret).isEmpty()) {
            throw new UnauthorizedException("Client authentication failed");
        }
        return new TokenResponse(tokens.issue(clientId), "Bearer", tokens.expirySeconds());
    }
}
