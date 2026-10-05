package com.workshop.employee.service;

import com.workshop.employee.repository.ApiClientRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Spring equivalent of {@code oauth-token-flow} (issue) and {@code validate-token-subflow} (check).
 *
 * <p>MIGRATION NOTE — the Mule app persisted the issued token by upserting
 * {@code api_clients.access_token / expires_at} ({@code ON CONFLICT (client_id) DO UPDATE}),
 * so each client holds exactly one live token and re-issuing silently revokes the previous
 * one. Those columns do not exist in the schema shipped with this repo, so the token store
 * is an in-memory map keyed by client_id that reproduces the same one-token-per-client
 * semantics. Tokens are lost on restart and not shared across instances — not production-grade.
 */
@Service
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    private final ApiClientRepository apiClientRepository;
    private final Clock clock;
    private final int tokenExpirySeconds;
    private final Map<String, IssuedToken> tokensByClientId = new ConcurrentHashMap<>();

    public OAuthService(ApiClientRepository apiClientRepository,
                        Clock clock,
                        @Value("${app.oauth.token-expiry-seconds:3600}") int tokenExpirySeconds) {
        this.apiClientRepository = apiClientRepository;
        this.clock = clock;
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    /** Validates credentials against {@code api_clients}; empty when no row matches. */
    public Optional<String> issueToken(String clientId, String clientSecret) {
        if (clientId == null || clientSecret == null) {
            return Optional.empty();
        }
        return apiClientRepository.findByClientIdAndClientSecret(clientId, clientSecret)
                .map(client -> {
                    Instant now = clock.instant();
                    // Mule: uuid() ++ "-" ++ now() as String
                    String accessToken = UUID.randomUUID() + "-" + now;
                    tokensByClientId.put(client.getClientId(),
                            new IssuedToken(accessToken, now.plusSeconds(tokenExpirySeconds)));
                    log.info("Token generated successfully for client: {}", clientId);
                    return accessToken;
                });
    }

    /** {@code WHERE access_token = :token AND expires_at > :currentTime} */
    public boolean isTokenValid(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return false;
        }
        Instant now = clock.instant();
        return tokensByClientId.values().stream()
                .anyMatch(issued -> issued.accessToken().equals(accessToken) && issued.expiresAt().isAfter(now));
    }

    public int getTokenExpirySeconds() {
        return tokenExpirySeconds;
    }

    private record IssuedToken(String accessToken, Instant expiresAt) {
    }
}
