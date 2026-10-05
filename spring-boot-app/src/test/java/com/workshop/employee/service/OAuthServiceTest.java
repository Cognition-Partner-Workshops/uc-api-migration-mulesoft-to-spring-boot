package com.workshop.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.workshop.employee.model.ApiClient;
import com.workshop.employee.repository.ApiClientRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthServiceTest {

    private static final Instant START = Instant.parse("2026-01-01T00:00:00Z");

    private final AtomicReference<Instant> now = new AtomicReference<>(START);
    private ApiClientRepository repository;
    private OAuthService service;

    @BeforeEach
    void setUp() {
        repository = mock(ApiClientRepository.class);
        ApiClient client = mock(ApiClient.class);
        when(client.getClientId()).thenReturn("demo-client");
        when(repository.findByClientIdAndClientSecret("demo-client", "demo-secret"))
                .thenReturn(Optional.of(client));
        when(repository.findByClientIdAndClientSecret("demo-client", "wrong"))
                .thenReturn(Optional.empty());
        when(repository.findByClientIdAndClientSecret(anyString(), anyString()))
                .thenAnswer(inv -> "demo-client".equals(inv.getArgument(0)) && "demo-secret".equals(inv.getArgument(1))
                        ? Optional.of(client) : Optional.empty());

        Clock clock = new Clock() {
            @Override public java.time.ZoneId getZone() { return ZoneOffset.UTC; }
            @Override public Clock withZone(java.time.ZoneId zone) { return this; }
            @Override public Instant instant() { return now.get(); }
        };
        service = new OAuthService(repository, clock, 3600);
    }

    @Test
    @DisplayName("issued token validates until expiry, then is rejected (expires_at > currentTime)")
    void tokenExpiresAfterConfiguredSeconds() {
        String token = service.issueToken("demo-client", "demo-secret").orElseThrow();

        assertThat(service.isTokenValid(token)).isTrue();

        now.set(START.plusSeconds(3599));
        assertThat(service.isTokenValid(token)).isTrue();

        now.set(START.plusSeconds(3600));
        assertThat(service.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("token format mirrors Mule: uuid() ++ \"-\" ++ now()")
    void tokenFormatMirrorsMule() {
        String token = service.issueToken("demo-client", "demo-secret").orElseThrow();
        assertThat(token).endsWith("-" + START);
        assertThat(token.substring(0, 36)).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    @DisplayName("wrong secret or null credentials never issue a token")
    void invalidCredentialsIssueNothing() {
        assertThat(service.issueToken("demo-client", "wrong")).isEmpty();
        assertThat(service.issueToken(null, "demo-secret")).isEmpty();
        assertThat(service.issueToken("demo-client", null)).isEmpty();
        assertThat(service.isTokenValid(null)).isFalse();
        assertThat(service.isTokenValid("")).isFalse();
    }
}
