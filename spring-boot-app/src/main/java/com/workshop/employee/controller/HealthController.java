package com.workshop.employee.controller;

import com.workshop.employee.dto.HealthResponse;
import com.workshop.employee.exception.ServiceUnavailableException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {
    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public HealthResponse health() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return new HealthResponse("UP", "UP");
        } catch (Exception ex) {
            throw new ServiceUnavailableException("Database unavailable", "DATABASE_DOWN");
        }
    }
}
