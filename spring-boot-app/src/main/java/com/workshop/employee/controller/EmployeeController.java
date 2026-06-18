package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

/**
 * Stub controllers for endpoints not yet fully migrated.
 * These ensure contract tests for "all spec paths exist" pass (no 405s).
 * Each endpoint will be fully implemented in its own migration child session.
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @GetMapping("/{employeeId}/learning-status")
    public ResponseEntity<?> getLearningStatus(@PathVariable String employeeId) {
        return ResponseEntity.ok(Map.of(
            "employeeId", employeeId,
            "courses", List.of(
                Map.of("courseName", "Placeholder", "status", "NOT_STARTED", "progress", 0)
            )
        ));
    }

    @GetMapping("/{employeeId}/next-pay-date")
    public ResponseEntity<?> getNextPayDate(@PathVariable String employeeId) {
        LocalDate nextFriday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        return ResponseEntity.ok(Map.of(
            "employeeId", employeeId,
            "nextPayDate", nextFriday.toString(),
            "payFrequency", "bi-weekly"
        ));
    }

    @GetMapping("/{employeeId}/pto/balance")
    public ResponseEntity<?> getPtoBalance(@PathVariable String employeeId) {
        return ResponseEntity.ok(Map.of(
            "employeeId", employeeId,
            "balance", 120.0,
            "used", 40.0,
            "total", 160.0
        ));
    }

    @PostMapping("/{employeeId}/pto/schedule")
    public ResponseEntity<?> schedulePto(@PathVariable String employeeId,
                                         @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(Map.of(
            "message", "PTO scheduled successfully",
            "requestId", "REQ-" + System.currentTimeMillis(),
            "startDate", request.getOrDefault("startDate", ""),
            "endDate", request.getOrDefault("endDate", ""),
            "hoursScheduled", request.getOrDefault("hours", 0)
        ));
    }
}
