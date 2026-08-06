package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.workshop.employee.service.TokenService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BearerTokenFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private FilterChain filterChain;

    private BearerTokenFilter filter;

    @BeforeEach
    void setUp() {
        filter = new BearerTokenFilter(tokenService, new ObjectMapper().registerModule(new JavaTimeModule()));
    }

    @Test
    void missingHeaderReturns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/employee/EMP001/goals");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("missing_token");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void invalidTokenReturns401() throws Exception {
        when(tokenService.isValid("bad-token")).thenReturn(false);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/employee/EMP001/goals");
        request.addHeader("Authorization", "Bearer bad-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("invalid_token");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void validTokenPassesThrough() throws Exception {
        when(tokenService.isValid("good-token")).thenReturn(true);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/employee/EMP001/goals");
        request.addHeader("Authorization", "Bearer good-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
