package com.workshop.employee.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private final JdbcTemplate jdbcTemplate;

    public HealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isDatabaseUp() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
