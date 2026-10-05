package com.workshop.employee.controller;

import com.workshop.employee.dto.HealthResponse;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Spring equivalent of {@code health-check-flow} ({@code GET /health}, no auth). */
@RestController
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    public HealthController(JdbcTemplate jdbcTemplate, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HealthResponse> health() {
        String timestamp = clock.instant().toString();
        try {
            jdbcTemplate.queryForObject("SELECT 1 AS health_check", Integer.class);
            return ResponseEntity.ok(new HealthResponse("UP", "connected", null, timestamp));
        } catch (Exception ex) {
            log.error("Health check failed: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new HealthResponse("DOWN", "disconnected", "Database connection failed", timestamp));
        }
    }
}
