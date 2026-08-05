package com.workshop.employee.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenStore {
    private record TokenDetails(String clientId, Instant expiresAt) {
    }
    private final Map<String, TokenDetails> tokens = new ConcurrentHashMap<>();
    private final long expirySeconds;

    public TokenStore(@Value("${app.oauth.token-expiry-seconds:3600}") long expirySeconds) {
        this.expirySeconds = expirySeconds;
    }

    public String issue(String clientId) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenDetails(clientId, Instant.now().plusSeconds(expirySeconds)));
        return token;
    }

    public boolean isValid(String token) {
        TokenDetails details = tokens.get(token);
        if (details == null) {
            return false;
        }
        if (details.expiresAt().isBefore(Instant.now())) {
            tokens.remove(token);
            return false;
        }
        return true;
    }

    public long expirySeconds() {
        return expirySeconds;
    }
}
