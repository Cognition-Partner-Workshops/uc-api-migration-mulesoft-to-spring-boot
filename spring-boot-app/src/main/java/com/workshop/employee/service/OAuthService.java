package com.workshop.employee.service;

import com.workshop.employee.repository.ApiClientRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    private final ApiClientRepository apiClientRepository;
    private final int tokenExpirySeconds;
    private final ConcurrentHashMap<String, TokenEntry> tokenStore = new ConcurrentHashMap<>();

    public OAuthService(ApiClientRepository apiClientRepository,
                        @Value("${app.oauth.token-expiry-seconds:3600}") int tokenExpirySeconds) {
        this.apiClientRepository = apiClientRepository;
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    public Optional<String> generateToken(String clientId, String clientSecret) {
        return apiClientRepository.findByClientIdAndClientSecret(clientId, clientSecret)
                .map(client -> {
                    String token = UUID.randomUUID() + "-" + Instant.now();
                    Instant expiresAt = Instant.now().plusSeconds(tokenExpirySeconds);
                    tokenStore.put(token, new TokenEntry(client.getClientId(), expiresAt));
                    log.info("Token generated for client: {}", clientId);
                    return token;
                });
    }

    public boolean validateToken(String token) {
        TokenEntry entry = tokenStore.get(token);
        if (entry == null) {
            return false;
        }
        if (Instant.now().isAfter(entry.expiresAt())) {
            tokenStore.remove(token);
            return false;
        }
        return true;
    }

    public int getTokenExpirySeconds() {
        return tokenExpirySeconds;
    }

    private record TokenEntry(String clientId, Instant expiresAt) {
    }
}
