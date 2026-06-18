package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.model.EmployeeLearning;
import com.workshop.employee.repository.EmployeeLearningRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee/{employeeId}")
public class EmployeeLearningController {

    private final EmployeeLearningRepository employeeLearningRepository;

    public EmployeeLearningController(EmployeeLearningRepository employeeLearningRepository) {
        this.employeeLearningRepository = employeeLearningRepository;
    }

    @GetMapping("/learning-status")
    public ResponseEntity<?> getLearningStatus(@PathVariable String employeeId) {
        List<EmployeeLearning> records = employeeLearningRepository.findByEmployeeId(employeeId);
        if (records.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("No learning records found for employee " + employeeId, "NOT_FOUND"));
        }
        List<Map<String, Object>> courses = records.stream()
                .map(r -> Map.<String, Object>of(
                        "courseName", r.getCourseName(),
                        "status", r.getStatus(),
                        "progress", r.getProgress()
                ))
                .toList();
        return ResponseEntity.ok(Map.of("employeeId", employeeId, "courses", courses));
    }
}
