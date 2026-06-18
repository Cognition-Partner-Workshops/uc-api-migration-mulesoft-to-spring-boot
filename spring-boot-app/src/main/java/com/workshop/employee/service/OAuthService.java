package com.workshop.employee.service;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.model.ApiClient;
import com.workshop.employee.repository.ApiClientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mirrors MuleSoft ObjectStore-based token management.
 * Tokens are stored in-memory (ConcurrentHashMap) — not production-grade,
 * but faithfully reproduces the MuleSoft ObjectStore behavior.
 */
@Service
public class OAuthService {

    private final ApiClientRepository apiClientRepository;
    private final int tokenExpirySeconds;

    // In-memory token store matching MuleSoft ObjectStore behavior
    private final Map<String, TokenEntry> tokenStore = new ConcurrentHashMap<>();

    public OAuthService(ApiClientRepository apiClientRepository,
                        @Value("${app.oauth.token-expiry-seconds:3600}") int tokenExpirySeconds) {
        this.apiClientRepository = apiClientRepository;
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    public Optional<TokenResponse> authenticate(String clientId, String clientSecret) {
        Optional<ApiClient> client = apiClientRepository.findByClientIdAndClientSecret(clientId, clientSecret);
        if (client.isEmpty()) {
            return Optional.empty();
        }

        String accessToken = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(tokenExpirySeconds);
        tokenStore.put(accessToken, new TokenEntry(clientId, expiresAt));

        return Optional.of(new TokenResponse(accessToken, "Bearer", tokenExpirySeconds));
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

    private record TokenEntry(String clientId, Instant expiresAt) {}
}
