package com.workshop.employee.service;

import com.workshop.employee.exception.UnauthorizedException;
import com.workshop.employee.repository.ApiClientRepository;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    @Test
    void validCredentialsIssueBearerToken() {
        ApiClientRepository repository = mock(ApiClientRepository.class);
        when(repository.findByClientIdAndClientSecret("id", "secret")).thenReturn(Optional.of(mock()));
        var result = new AuthService(repository, new TokenStore(3600))
            .issueToken("client_credentials", "id", "secret");
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.expiresIn()).isEqualTo(3600);
    }

    @Test
    void invalidGrantIsUnauthorized() {
        var service = new AuthService(mock(ApiClientRepository.class), new TokenStore(3600));
        assertThatThrownBy(() -> service.issueToken("password", "id", "secret"))
            .isInstanceOf(UnauthorizedException.class);
    }
}
