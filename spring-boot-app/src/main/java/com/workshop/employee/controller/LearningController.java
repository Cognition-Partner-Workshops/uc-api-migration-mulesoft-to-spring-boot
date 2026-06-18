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
@RequestMapping("/api/employee")
public class LearningController {

    private final LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping("/{employeeId}/learning-status")
    public ResponseEntity<?> getLearningStatus(@PathVariable String employeeId) {
        Optional<LearningStatusResponse> result = learningService.getLearningStatus(employeeId);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        // MuleSoft source returns: {"error": "Learning records not found", "employeeId": "..."}
        // Contract specifies ErrorResponse with message field
        return ResponseEntity.status(404)
                .body(new ErrorResponse("Learning records not found for employee " + employeeId, "NOT_FOUND"));
    }
}
