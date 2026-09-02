package com.workshop.employee;

import com.workshop.employee.model.ApiClient;
import com.workshop.employee.repository.ApiClientRepository;
import com.workshop.employee.service.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TokenServiceTest {
    @Test
    void issueAndValidateToken() {
        ApiClientRepository repository = Mockito.mock(ApiClientRepository.class);
        when(repository.findByClientIdAndClientSecret("client", "secret")).thenReturn(Optional.of(Mockito.mock(ApiClient.class)));
        TokenService service = new TokenService(repository, 3600);

        String token = service.issueToken("client", "secret");
        assertTrue(service.validate(token));
        assertFalse(service.validate("unknown"));
    }

    @Test
    void expiredTokenIsRejected() throws InterruptedException {
        ApiClientRepository repository = Mockito.mock(ApiClientRepository.class);
        when(repository.findByClientIdAndClientSecret("client", "secret")).thenReturn(Optional.of(Mockito.mock(ApiClient.class)));
        TokenService service = new TokenService(repository, 0);

        String token = service.issueToken("client", "secret");
        Thread.sleep(5);
        assertFalse(service.validate(token));
    }
}
