package com.workshop.employee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        try (Connection conn = dataSource.getConnection()) {
            boolean valid = conn.isValid(5);
            if (valid) {
                return ResponseEntity.ok(Map.of(
                        "status", "UP",
                        "database", "connected"
                ));
            }
        } catch (Exception e) {
            // fall through to DOWN
        }
        return ResponseEntity.status(503).body(Map.of(
                "status", "DOWN",
                "database", "disconnected"
        ));
    }
}
