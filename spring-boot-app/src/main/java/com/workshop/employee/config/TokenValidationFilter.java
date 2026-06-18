package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Token validation filter for all /api/** endpoints.
 * Reproduces MuleSoft validate-token-subflow behavior:
 * - Checks Authorization header for Bearer token
 * - Validates token against DB (not expired, exists)
 * - Returns 401 ErrorResponse on missing/invalid token
 */
@Component
public class TokenValidationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TokenValidationFilter.class);

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    public TokenValidationFilter(TokenService tokenService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Only filter /api/** paths; /oauth/token and /health are unprotected
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            writeErrorResponse(response, "Authorization header is missing or invalid", "MISSING_TOKEN");
            return;
        }

        String token = authHeader.substring(7);

        if (!tokenService.validateToken(token)) {
            log.warn("Invalid or expired access token");
            writeErrorResponse(response, "Invalid or expired access token", "INVALID_TOKEN");
            return;
        }

        log.debug("Token validation successful");
        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, String message, String errorCode)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                ErrorResponse.of(message, errorCode));
    }
}
