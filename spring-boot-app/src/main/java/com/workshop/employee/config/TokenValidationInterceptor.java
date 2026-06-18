package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Validates Bearer tokens for all /api/** endpoints.
 * Mirrors the MuleSoft validate-token-subflow behavior.
 */
@Component
public class TokenValidationInterceptor implements HandlerInterceptor {

    private final OAuthService oAuthService;
    private final ObjectMapper objectMapper;

    public TokenValidationInterceptor(OAuthService oAuthService, ObjectMapper objectMapper) {
        this.oAuthService = oAuthService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, "Missing or invalid authorization token");
            return false;
        }

        String token = authHeader.substring(7);
        if (!oAuthService.validateToken(token)) {
            writeUnauthorized(response, "Token is invalid or expired");
            return false;
        }

        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse error = new ErrorResponse(message, "UNAUTHORIZED");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
