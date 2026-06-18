package com.workshop.employee.service;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.model.ApiClient;
import com.workshop.employee.repository.ApiClientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class OAuthService {

    private final ApiClientRepository apiClientRepository;
    private final int tokenExpirySeconds;

    public OAuthService(ApiClientRepository apiClientRepository,
                        @Value("${app.oauth.token-expiry-seconds}") int tokenExpirySeconds) {
        this.apiClientRepository = apiClientRepository;
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    /**
     * Validates client credentials and generates a bearer token.
     * Mirrors the MuleSoft oauth-token-flow: validates against api_clients table,
     * generates a UUID-based token, and stores it with expiration in the DB.
     */
    public Optional<TokenResponse> generateToken(String clientId, String clientSecret) {
        Optional<ApiClient> clientOpt = apiClientRepository.findByClientIdAndClientSecret(clientId, clientSecret);
        if (clientOpt.isEmpty()) {
            return Optional.empty();
        }

        ApiClient client = clientOpt.get();
        String accessToken = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(tokenExpirySeconds);

        client.setAccessToken(accessToken);
        client.setExpiresAt(expiresAt);
        apiClientRepository.save(client);

        return Optional.of(new TokenResponse(accessToken, "Bearer", tokenExpirySeconds));
    }

    /**
     * Validates a bearer token against the DB.
     * Mirrors the MuleSoft validate-token-subflow: checks access_token exists
     * and expires_at > now.
     */
    public boolean validateToken(String token) {
        return apiClientRepository.findByAccessTokenAndExpiresAtAfter(token, LocalDateTime.now()).isPresent();
    }
}
