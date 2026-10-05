package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Spring equivalent of the MuleSoft {@code validate-token-subflow}.
 *
 * <p>Runs only for requests that matched a controller method. In the Mule app the
 * APIKit router resolves the resource before the flow calls the token sub-flow, so an
 * unknown {@code /api/...} path yields {@code 404 Resource not found} rather than 401 —
 * skipping non-{@link HandlerMethod} handlers reproduces that ordering.
 */
@Component
public class TokenValidationInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TokenValidationInterceptor.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final OAuthService oAuthService;
    private final ObjectMapper objectMapper;

    public TokenValidationInterceptor(OAuthService oAuthService, ObjectMapper objectMapper) {
        this.oAuthService = oAuthService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header");
            sendUnauthorized(response, "missing_token", "Authorization header is missing or invalid");
            return false;
        }

        String bearerToken = authHeader.substring(BEARER_PREFIX.length());
        if (!oAuthService.isTokenValid(bearerToken)) {
            sendUnauthorized(response, "invalid_token", "Invalid or expired access token");
            return false;
        }
        return true;
    }

    private void sendUnauthorized(HttpServletResponse response, String errorCode, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ErrorResponse.of(message, errorCode));
    }
}
