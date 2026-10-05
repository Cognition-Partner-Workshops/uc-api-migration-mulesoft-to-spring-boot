package com.workshop.employee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Mirrors the MuleSoft listener layout: {@code /api/*} is routed by APIKit and each
 * routed flow calls {@code validate-token-subflow}; {@code /oauth/token} and
 * {@code /health} are separate listeners with no token check.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenValidationInterceptor tokenValidationInterceptor;

    public WebConfig(TokenValidationInterceptor tokenValidationInterceptor) {
        this.tokenValidationInterceptor = tokenValidationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidationInterceptor).addPathPatterns("/api/**");
    }
}
