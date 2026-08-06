package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.employee.service.TokenService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<BearerTokenFilter> bearerTokenFilter(
            TokenService tokenService, ObjectMapper objectMapper) {
        FilterRegistrationBean<BearerTokenFilter> registration =
                new FilterRegistrationBean<>(new BearerTokenFilter(tokenService, objectMapper));
        registration.addUrlPatterns("/api/*");
        return registration;
    }
}
