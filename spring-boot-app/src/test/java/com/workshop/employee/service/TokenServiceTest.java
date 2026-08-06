package com.workshop.employee.service;

import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.exception.UnauthorizedException;
import com.workshop.employee.model.ApiClient;
import com.workshop.employee.repository.ApiClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private ApiClientRepository apiClientRepository;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(apiClientRepository, 3600);
    }

    @Test
    void issuesTokenForValidCredentials() {
        when(apiClientRepository.findByClientIdAndClientSecret("demo-client", "demo-secret"))
                .thenReturn(Optional.of(new ApiClient()));

        TokenResponse response = tokenService.issueToken("client_credentials", "demo-client", "demo-secret");

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600);
        assertThat(tokenService.isValid(response.accessToken())).isTrue();
    }

    @Test
    void rejectsInvalidCredentials() {
        when(apiClientRepository.findByClientIdAndClientSecret(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> tokenService.issueToken("client_credentials", "bad", "bad"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void rejectsUnsupportedGrantType() {
        assertThatThrownBy(() -> tokenService.issueToken("password", "demo-client", "demo-secret"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void unknownTokenIsInvalid() {
        assertThat(tokenService.isValid("no-such-token")).isFalse();
        assertThat(tokenService.isValid(null)).isFalse();
    }

    @Test
    void expiredTokenIsInvalid() {
        when(apiClientRepository.findByClientIdAndClientSecret("demo-client", "demo-secret"))
                .thenReturn(Optional.of(new ApiClient()));
        TokenService shortLived = new TokenService(apiClientRepository, -1);

        TokenResponse response = shortLived.issueToken("client_credentials", "demo-client", "demo-secret");

        assertThat(shortLived.isValid(response.accessToken())).isFalse();
    }
}
