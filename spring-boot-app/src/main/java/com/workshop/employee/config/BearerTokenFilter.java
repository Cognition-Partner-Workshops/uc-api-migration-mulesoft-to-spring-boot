package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bearer-token protection for /api/** endpoints, mirroring the MuleSoft
 * validate-token-subflow behavior (401 on missing or invalid tokens).
 */
public class BearerTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    public BearerTokenFilter(TokenService tokenService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            writeUnauthorized(response, "Authorization header is missing or invalid", "missing_token");
            return;
        }
        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!tokenService.isValid(token)) {
            writeUnauthorized(response, "Invalid or expired access token", "invalid_token");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message, String errorCode)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(message, errorCode));
    }
}
