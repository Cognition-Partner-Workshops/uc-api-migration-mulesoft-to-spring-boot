package com.workshop.employee.controller;

import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        try (var conn = dataSource.getConnection()) {
            conn.isValid(2);
            return ResponseEntity.ok(Map.of("status", "UP", "database", "connected"));
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            return ResponseEntity.status(503)
                    .body(Map.of("status", "DOWN", "database", "disconnected"));
        }
    }
}
