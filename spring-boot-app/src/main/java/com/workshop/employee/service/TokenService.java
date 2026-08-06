package com.workshop.employee.service;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.exception.UnauthorizedException;
import com.workshop.employee.repository.ApiClientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory OAuth2 client-credentials token management.
 * Mirrors the MuleSoft oauth-token-flow and validate-token-subflow:
 * clients are validated against the api_clients table and issued
 * opaque bearer tokens with a 1-hour expiry.
 */
@Service
public class TokenService {

    private record TokenInfo(String clientId, Instant expiresAt) {
    }

    private final ApiClientRepository apiClientRepository;
    private final int tokenExpirySeconds;
    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    public TokenService(ApiClientRepository apiClientRepository,
                        @Value("${app.oauth.token-expiry-seconds:3600}") int tokenExpirySeconds) {
        this.apiClientRepository = apiClientRepository;
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    public TokenResponse issueToken(String grantType, String clientId, String clientSecret) {
        if (!"client_credentials".equals(grantType)) {
            throw new UnauthorizedException("Unsupported grant type", "unsupported_grant_type");
        }
        apiClientRepository.findByClientIdAndClientSecret(clientId, clientSecret)
                .orElseThrow(() -> new UnauthorizedException(
                        "Client authentication failed", "invalid_client"));

        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenInfo(clientId, Instant.now().plusSeconds(tokenExpirySeconds)));
        return new TokenResponse(token, "Bearer", tokenExpirySeconds);
    }

    public boolean isValid(String token) {
        if (token == null) {
            return false;
        }
        TokenInfo info = tokens.get(token);
        if (info == null) {
            return false;
        }
        if (info.expiresAt().isBefore(Instant.now())) {
            tokens.remove(token);
            return false;
        }
        return true;
    }
}
