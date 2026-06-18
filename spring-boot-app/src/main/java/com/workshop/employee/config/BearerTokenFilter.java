package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.service.OAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Mirrors the MuleSoft validate-token-subflow: extracts Bearer token from
 * Authorization header and validates it against the in-memory token store.
 * Returns 401 with JSON error body on missing/invalid token.
 */
@Component
public class BearerTokenFilter extends OncePerRequestFilter {

    private final OAuthService oAuthService;
    private final ObjectMapper objectMapper;

    public BearerTokenFilter(OAuthService oAuthService, ObjectMapper objectMapper) {
        this.oAuthService = oAuthService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, "Authorization header is missing or invalid");
            return;
        }

        String token = authHeader.substring(7);
        if (!oAuthService.validateToken(token)) {
            writeUnauthorized(response, "Invalid or expired access token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse error = new ErrorResponse(message, "UNAUTHORIZED");
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
