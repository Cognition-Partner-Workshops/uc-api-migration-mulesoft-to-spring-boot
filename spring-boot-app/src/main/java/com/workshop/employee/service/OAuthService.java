package com.workshop.employee.service;

import com.workshop.employee.model.ApiClient;
import com.workshop.employee.repository.ApiClientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth2 token management — mirrors MuleSoft Object Store token behavior.
 * Tokens are stored in-memory (ConcurrentHashMap) matching the source's
 * ObjectStore approach. Not production-grade.
 */
@Service
public class OAuthService {

    private final ApiClientRepository apiClientRepository;
    private final ConcurrentHashMap<String, Long> tokenStore = new ConcurrentHashMap<>();

    @Value("${app.oauth.token-expiry-seconds:3600}")
    private int tokenExpirySeconds;

    public OAuthService(ApiClientRepository apiClientRepository) {
        this.apiClientRepository = apiClientRepository;
    }

    public Optional<String> authenticate(String clientId, String clientSecret) {
        Optional<ApiClient> client = apiClientRepository.findByClientIdAndClientSecret(clientId, clientSecret);
        if (client.isEmpty()) {
            return Optional.empty();
        }
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, System.currentTimeMillis() + (tokenExpirySeconds * 1000L));
        return Optional.of(token);
    }

    public boolean validateToken(String token) {
        Long expiry = tokenStore.get(token);
        if (expiry == null) {
            return false;
        }
        if (System.currentTimeMillis() > expiry) {
            tokenStore.remove(token);
            return false;
        }
        return true;
    }

    public int getTokenExpirySeconds() {
        return tokenExpirySeconds;
    }
}
