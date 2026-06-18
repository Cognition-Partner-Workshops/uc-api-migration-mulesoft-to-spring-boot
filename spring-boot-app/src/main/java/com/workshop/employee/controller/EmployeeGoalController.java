package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.service.EmployeeGoalService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee/{employeeId}")
public class EmployeeGoalController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeGoalController.class);

    private final EmployeeGoalService employeeGoalService;

    public EmployeeGoalController(EmployeeGoalService employeeGoalService) {
        this.employeeGoalService = employeeGoalService;
    }

    @GetMapping("/goals")
    public ResponseEntity<?> getGoals(@PathVariable String employeeId) {
        log.info("Processing request for employee goals: {}", employeeId);

        List<String> goals = employeeGoalService.getGoals(employeeId);

        if (goals.isEmpty()) {
            log.warn("No goals found for employee: {}", employeeId);
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("No goals found for employee " + employeeId));
        }

        return ResponseEntity.ok(goals);
    }
}
