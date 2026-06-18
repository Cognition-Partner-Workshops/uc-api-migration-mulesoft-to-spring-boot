package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.service.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping("/{employeeId}/goals")
    public ResponseEntity<?> getGoals(@PathVariable String employeeId) {
        List<String> goals = goalService.getGoals(employeeId);
        if (goals.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("No goals found for employee " + employeeId, "NOT_FOUND"));
        }
        return ResponseEntity.ok(goals);
    }
}
