package com.workshop.employee.service;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.model.ApiClient;
import com.workshop.employee.repository.ApiClientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth2 client-credentials service.
 *
 * Token storage uses an in-memory ConcurrentHashMap, matching the MuleSoft
 * ObjectStore behavior. This is NOT production-grade — a real deployment would
 * use Redis or a database-backed token store.
 */
@Service
public class OAuthService {

    private final ApiClientRepository apiClientRepository;
    private final int tokenExpirySeconds;

    // In-memory token store (mirrors MuleSoft ObjectStore behavior)
    private final ConcurrentHashMap<String, Instant> tokenStore = new ConcurrentHashMap<>();

    public OAuthService(ApiClientRepository apiClientRepository,
                        @Value("${app.oauth.token-expiry-seconds:3600}") int tokenExpirySeconds) {
        this.apiClientRepository = apiClientRepository;
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    public TokenResponse generateToken(String clientId, String clientSecret) {
        Optional<ApiClient> client = apiClientRepository.findByClientIdAndClientSecret(clientId, clientSecret);
        if (client.isEmpty()) {
            return null;
        }

        String accessToken = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(tokenExpirySeconds);
        tokenStore.put(accessToken, expiry);

        return new TokenResponse(accessToken, "Bearer", tokenExpirySeconds);
    }

    public boolean validateToken(String token) {
        Instant expiry = tokenStore.get(token);
        if (expiry == null) {
            return false;
        }
        if (Instant.now().isAfter(expiry)) {
            tokenStore.remove(token);
            return false;
        }
        return true;
    }
}
