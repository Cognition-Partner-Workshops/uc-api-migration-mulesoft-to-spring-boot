package com.workshop.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.employee.service.TokenStore;
import java.time.Clock;
import java.time.ZoneOffset;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    Clock clock() {
        return Clock.system(ZoneOffset.UTC);
    }

    @Bean
    FilterRegistrationBean<ApiAuthenticationFilter> apiAuthenticationFilter(
        TokenStore tokenStore, ObjectMapper objectMapper) {
        FilterRegistrationBean<ApiAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ApiAuthenticationFilter(tokenStore, objectMapper));
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}
