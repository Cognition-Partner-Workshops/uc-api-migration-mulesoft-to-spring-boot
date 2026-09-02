package com.workshop.employee.mock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MOCK — development-only stand-in for the real backend; delete at integration time.
 */
@Profile("mock")
@RestController
public class MockEmployeeApiController {

    private static final Set<String> ISSUED_TOKENS = ConcurrentHashMap.newKeySet();

    private static final Map<String, EmployeeData> EMPLOYEES = Map.of(
            "1001", new EmployeeData(
                    List.of(
                            "Build an Agentforce Agent using the new MCP protocol for integrations.",
                            "Improve team delivery predictability through better sprint planning.",
                            "Mentor two engineers through their next development milestone."),
                    List.of(
                            new CourseStatus("Spring Boot 3 Fundamentals", "COMPLETED", 100),
                            new CourseStatus("Secure API Design", "IN_PROGRESS", 65),
                            new CourseStatus("Leadership Essentials", "NOT_STARTED", 0)),
                    "2025-09-12", "BI_WEEKLY", 80, 40, 120),
            "1002", new EmployeeData(
                    List.of(
                            "Lead the customer onboarding automation initiative.",
                            "Complete the annual data privacy and security objectives.",
                            "Present a quarterly innovation proposal to the engineering group."),
                    List.of(
                            new CourseStatus("Cloud Architecture", "IN_PROGRESS", 45),
                            new CourseStatus("Data Privacy Essentials", "COMPLETED", 100),
                            new CourseStatus("Effective Communication", "NOT_STARTED", 0)),
                    "2025-09-30", "MONTHLY", 16, 104, 120),
            "1003", new EmployeeData(
                    List.of(
                            "Deliver the employee directory migration ahead of schedule.",
                            "Document the service integration patterns for the platform team.",
                            "Earn an advanced certification in cloud-native development."),
                    List.of(
                            new CourseStatus("Cloud-Native Development", "COMPLETED", 100),
                            new CourseStatus("MuleSoft to Spring Boot Migration", "IN_PROGRESS", 70),
                            new CourseStatus("Advanced Testing Strategies", "NOT_STARTED", 0)),
                    "2025-09-15", "BI_WEEKLY", 120, 0, 120));

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> token(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret) {
        if (!"client_credentials".equals(grantType)
                || !"demo-client".equals(clientId)
                || !"demo-secret".equals(clientSecret)) {
            return error(HttpStatus.UNAUTHORIZED, "Invalid client credentials", "INVALID_CLIENT");
        }

        String token = "mock-token-" + UUID.randomUUID();
        ISSUED_TOKENS.add(token);
        return ResponseEntity.ok(Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", 3600));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "database", "UP");
    }

    @GetMapping("/api/employee/{employeeId}/goals")
    public ResponseEntity<?> goals(
            @PathVariable String employeeId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Optional<ResponseEntity<Object>> unauthorized = authorize(authorization);
        if (unauthorized.isPresent()) {
            return unauthorized.get();
        }

        EmployeeData employee = EMPLOYEES.get(employeeId);
        if (employee == null) {
            return error(HttpStatus.NOT_FOUND, "No goals found for employee " + employeeId, "NOT_FOUND");
        }
        return ResponseEntity.ok(employee.goals);
    }

    @GetMapping("/api/employee/{employeeId}/learning-status")
    public ResponseEntity<?> learningStatus(
            @PathVariable String employeeId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Optional<ResponseEntity<Object>> unauthorized = authorize(authorization);
        if (unauthorized.isPresent()) {
            return unauthorized.get();
        }

        EmployeeData employee = EMPLOYEES.get(employeeId);
        if (employee == null) {
            return error(HttpStatus.NOT_FOUND,
                    "No learning records found for employee " + employeeId, "NOT_FOUND");
        }
        return ResponseEntity.ok(Map.of("employeeId", employeeId, "courses", employee.courses));
    }

    @GetMapping("/api/employee/{employeeId}/next-pay-date")
    public ResponseEntity<?> nextPayDate(
            @PathVariable String employeeId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Optional<ResponseEntity<Object>> unauthorized = authorize(authorization);
        if (unauthorized.isPresent()) {
            return unauthorized.get();
        }

        EmployeeData employee = EMPLOYEES.get(employeeId);
        if (employee == null) {
            return error(HttpStatus.NOT_FOUND, "Employee " + employeeId + " not found", "NOT_FOUND");
        }
        return ResponseEntity.ok(Map.of(
                "employeeId", employeeId,
                "nextPayDate", employee.nextPayDate,
                "payFrequency", employee.payFrequency));
    }

    @GetMapping("/api/employee/{employeeId}/pto/balance")
    public ResponseEntity<?> ptoBalance(
            @PathVariable String employeeId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Optional<ResponseEntity<Object>> unauthorized = authorize(authorization);
        if (unauthorized.isPresent()) {
            return unauthorized.get();
        }

        EmployeeData employee = EMPLOYEES.get(employeeId);
        if (employee == null) {
            return error(HttpStatus.NOT_FOUND, "Employee " + employeeId + " not found", "NOT_FOUND");
        }
        return ResponseEntity.ok(ptoBalanceBody(employeeId, employee));
    }

    @PostMapping("/api/employee/{employeeId}/pto/schedule")
    public ResponseEntity<?> schedulePto(
            @PathVariable String employeeId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) PtoScheduleRequest request) {
        Optional<ResponseEntity<Object>> unauthorized = authorize(authorization);
        if (unauthorized.isPresent()) {
            return unauthorized.get();
        }

        EmployeeData employee = EMPLOYEES.get(employeeId);
        if (employee == null) {
            return error(HttpStatus.NOT_FOUND, "Employee " + employeeId + " not found", "NOT_FOUND");
        }
        if (request == null || request.hours() == null || request.hours() <= 0) {
            return error(HttpStatus.BAD_REQUEST, "hours must be positive", "INVALID_HOURS");
        }

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(request.startDate());
            endDate = LocalDate.parse(request.endDate());
        } catch (DateTimeParseException | NullPointerException exception) {
            return error(HttpStatus.BAD_REQUEST,
                    "Invalid date range: endDate must not be before startDate", "INVALID_DATES");
        }
        if (endDate.isBefore(startDate)) {
            return error(HttpStatus.BAD_REQUEST,
                    "Invalid date range: endDate must not be before startDate", "INVALID_DATES");
        }

        synchronized (employee) {
            if (request.hours() > employee.balance) {
                return error(HttpStatus.BAD_REQUEST,
                        "Insufficient PTO balance: requested " + request.hours()
                                + " hours, available " + employee.balance,
                        "INSUFFICIENT_BALANCE");
            }
            employee.balance -= request.hours();
            employee.used += request.hours();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "PTO scheduled successfully");
        response.put("requestId", "PTO-" + UUID.randomUUID().toString().substring(0, 8));
        response.put("startDate", request.startDate());
        response.put("endDate", request.endDate());
        response.put("hoursScheduled", request.hours());
        return ResponseEntity.ok(response);
    }

    private Optional<ResponseEntity<Object>> authorize(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Optional.of(error(HttpStatus.UNAUTHORIZED,
                    "Invalid or missing token", "UNAUTHORIZED"));
        }

        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isEmpty() || !ISSUED_TOKENS.contains(token)) {
            return Optional.of(error(HttpStatus.UNAUTHORIZED,
                    "Invalid or missing token", "UNAUTHORIZED"));
        }
        return Optional.empty();
    }

    private ResponseEntity<Object> error(HttpStatus status, String message, String errorCode) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("errorCode", errorCode);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(body);
    }

    private Map<String, Object> ptoBalanceBody(String employeeId, EmployeeData employee) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("employeeId", employeeId);
        body.put("balance", employee.balance);
        body.put("used", employee.used);
        body.put("total", employee.total);
        return body;
    }

    private static final class EmployeeData {
        private final List<String> goals;
        private final List<CourseStatus> courses;
        private final String nextPayDate;
        private final String payFrequency;
        private double balance;
        private double used;
        private final double total;

        private EmployeeData(
                List<String> goals,
                List<CourseStatus> courses,
                String nextPayDate,
                String payFrequency,
                double balance,
                double used,
                double total) {
            this.goals = goals;
            this.courses = courses;
            this.nextPayDate = nextPayDate;
            this.payFrequency = payFrequency;
            this.balance = balance;
            this.used = used;
            this.total = total;
        }
    }

    private record CourseStatus(String courseName, String status, int progress) {
    }

    public record PtoScheduleRequest(String startDate, String endDate, Double hours) {
    }
}
