package com.workshop.employee.controller;

import com.workshop.employee.service.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Mirrors MuleSoft flow: get:\employee\(employeeId)\goals:employee-services-api-config
 *
 * Returns an array of goal description strings for the given employee.
 * Bearer token validation is handled by BearerTokenFilter for all /api/** paths.
 */
@RestController
@RequestMapping("/api/employee")
public class GoalsController {

    private final GoalService goalService;

    public GoalsController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping("/{employeeId}/goals")
    public ResponseEntity<List<String>> getGoals(@PathVariable String employeeId) {
        List<String> goals = goalService.getGoalsForEmployee(employeeId);
        return ResponseEntity.ok(goals);
    }
}
