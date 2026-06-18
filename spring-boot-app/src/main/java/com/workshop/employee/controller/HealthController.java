package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
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

    /**
     * GET /health — database connectivity check.
     * Reproduces MuleSoft health-check-flow: SELECT 1 as health_check.
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT 1 AS health_check")) {
            rs.next();
            return ResponseEntity.ok(Map.of("status", "UP", "database", "connected"));
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            return ResponseEntity.status(503)
                    .body(ErrorResponse.of("Database connectivity failure", "DB_UNAVAILABLE"));
        }
    }
}
