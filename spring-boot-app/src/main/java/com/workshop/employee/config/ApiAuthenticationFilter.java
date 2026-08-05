package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.employee.exception.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import com.workshop.employee.service.TokenStore;

public class ApiAuthenticationFilter extends OncePerRequestFilter {
    private final TokenStore tokenStore;
    private final ObjectMapper objectMapper;

    public ApiAuthenticationFilter(TokenStore tokenStore, ObjectMapper objectMapper) {
        this.tokenStore = tokenStore;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = header != null && header.startsWith("Bearer ") ? header.substring(7).trim() : "";
        if (!tokenStore.isValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(),
                new ApiError("Missing or invalid access token", "UNAUTHORIZED", Instant.now()));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
