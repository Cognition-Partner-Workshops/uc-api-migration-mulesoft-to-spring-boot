package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.dto.LearningStatusResponse;
import com.workshop.employee.service.LearningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/employee/{employeeId}")
public class LearningController {

    private final LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping("/learning-status")
    public ResponseEntity<?> getLearningStatus(@PathVariable String employeeId) {
        Optional<LearningStatusResponse> result = learningService.getLearningStatus(employeeId);
        if (result.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse(
                            "No learning records found for employee " + employeeId,
                            "LEARNING_NOT_FOUND"));
        }
        return ResponseEntity.ok(result.get());
    }
}
