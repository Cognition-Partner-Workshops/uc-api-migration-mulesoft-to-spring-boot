package com.workshop.employee.service;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.exception.InvalidClientException;
import com.workshop.employee.model.ApiClient;
import com.workshop.employee.repository.ApiClientRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private final ApiClientRepository apiClientRepository;
    private final int tokenExpirySeconds;

    public TokenService(ApiClientRepository apiClientRepository,
                        @Value("${app.oauth.token-expiry-seconds:3600}") int tokenExpirySeconds) {
        this.apiClientRepository = apiClientRepository;
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    /**
     * Authenticate client credentials and generate an access token.
     * Reproduces MuleSoft oauth-token-flow behavior:
     * - Validate against api_clients table
     * - Generate UUID + timestamp token (MuleSoft: uuid() ++ "-" ++ now())
     * - Store token in DB with expiration (ON CONFLICT DO UPDATE)
     */
    @Transactional
    public TokenResponse authenticate(String clientId, String clientSecret) {
        log.info("Validating client credentials for client: {}", clientId);

        ApiClient apiClient = apiClientRepository.findByClientIdAndClientSecret(clientId, clientSecret)
                .orElseThrow(() -> {
                    log.warn("Invalid client credentials: {}", clientId);
                    return new InvalidClientException("Client authentication failed");
                });

        // Token format matches MuleSoft: uuid() ++ "-" ++ now() as String
        String accessToken = UUID.randomUUID() + "-" + Instant.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(tokenExpirySeconds);

        apiClient.setAccessToken(accessToken);
        apiClient.setExpiresAt(expiresAt);
        apiClientRepository.save(apiClient);

        log.info("Token generated successfully for client: {}", clientId);

        return new TokenResponse(accessToken, "Bearer", tokenExpirySeconds);
    }

    /**
     * Validate a bearer token against the database.
     * Reproduces MuleSoft validate-token-subflow:
     * - SELECT WHERE access_token = ? AND expires_at > NOW()
     */
    public boolean validateToken(String token) {
        return apiClientRepository
                .findByAccessTokenAndExpiresAtAfter(token, LocalDateTime.now())
                .isPresent();
    }
}
