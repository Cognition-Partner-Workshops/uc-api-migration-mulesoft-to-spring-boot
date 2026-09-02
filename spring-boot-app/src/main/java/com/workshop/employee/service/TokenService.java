package com.workshop.employee.service;

import com.workshop.employee.exception.UnauthorizedException;
import com.workshop.employee.repository.ApiClientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private final ApiClientRepository apiClientRepository;
    private final long tokenExpirySeconds;
    private final Map<String, Instant> tokens = new ConcurrentHashMap<>();

    public TokenService(ApiClientRepository apiClientRepository,
                        @Value("${app.oauth.token-expiry-seconds:3600}") long tokenExpirySeconds) {
        this.apiClientRepository = apiClientRepository;
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    public String issueToken(String clientId, String clientSecret) {
        apiClientRepository.findByClientIdAndClientSecret(clientId, clientSecret)
                .orElseThrow(() -> new UnauthorizedException("Invalid client credentials", "INVALID_CLIENT"));
        String token = Base64.getUrlEncoder().withoutPadding()
                .encodeToString((UUID.randomUUID() + UUID.randomUUID().toString()).getBytes());
        tokens.put(token, Instant.now().plusSeconds(tokenExpirySeconds));
        return token;
    }

    public boolean validate(String token) {
        if (token == null) {
            return false;
        }
        Instant expiry = tokens.get(token);
        if (expiry == null) {
            return false;
        }
        if (expiry.isBefore(Instant.now())) {
            tokens.remove(token, expiry);
            return false;
        }
        return true;
    }

    public long getTokenExpirySeconds() {
        return tokenExpirySeconds;
    }
}
