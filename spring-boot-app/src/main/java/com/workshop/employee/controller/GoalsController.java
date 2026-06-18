package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.service.GoalsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee/{employeeId}")
public class GoalsController {

    private final GoalsService goalsService;

    public GoalsController(GoalsService goalsService) {
        this.goalsService = goalsService;
    }

    @GetMapping("/goals")
    public ResponseEntity<?> getGoals(@PathVariable String employeeId) {
        List<String> goals = goalsService.getGoals(employeeId);
        if (goals.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse(
                            "No goals found for employee " + employeeId,
                            "GOALS_NOT_FOUND"));
        }
        return ResponseEntity.ok(goals);
    }
}
