package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.workshop.employee.service.TokenStore;
import jakarta.servlet.FilterChain;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ApiAuthenticationFilterTest {
    private final TokenStore tokenStore = new TokenStore(3600);
    private final ApiAuthenticationFilter filter =
        new ApiAuthenticationFilter(tokenStore, new ObjectMapper().registerModule(new JavaTimeModule()));

    @Test
    void validTokenPassesThroughChain() throws Exception {
        String token = tokenStore.issue("client");
        MockHttpServletRequest request = requestWithToken(token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void missingTokenReturnsUnauthorizedJson() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest(), response, mock(FilterChain.class));

        assertUnauthorized(response);
    }

    @Test
    void invalidTokenReturnsUnauthorizedJson() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(requestWithToken("invalid"), response, mock(FilterChain.class));

        assertUnauthorized(response);
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }

    private void assertUnauthorized(MockHttpServletResponse response) throws Exception {
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");
        Map<String, Object> body = new ObjectMapper().readValue(response.getContentAsString(),
            new TypeReference<>() {
            });
        assertThat(body).containsEntry("message", "Missing or invalid access token");
        assertThat(body).containsEntry("errorCode", "UNAUTHORIZED");
        assertThat(body).containsKey("timestamp");
    }
}
