package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.model.EmployeeGoal;
import com.workshop.employee.repository.EmployeeGoalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/{employeeId}")
public class EmployeeGoalsController {

    private final EmployeeGoalRepository employeeGoalRepository;

    public EmployeeGoalsController(EmployeeGoalRepository employeeGoalRepository) {
        this.employeeGoalRepository = employeeGoalRepository;
    }

    @GetMapping("/goals")
    public ResponseEntity<?> getGoals(@PathVariable String employeeId) {
        List<EmployeeGoal> goals = employeeGoalRepository.findByEmployeeId(employeeId);
        if (goals.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("No goals found for employee " + employeeId, "NOT_FOUND"));
        }
        List<String> goalDescriptions = goals.stream()
                .map(EmployeeGoal::getGoalDescription)
                .toList();
        return ResponseEntity.ok(goalDescriptions);
    }
}
