package com.workshop.employee.controller;

import com.workshop.employee.dto.LearningStatus;
import com.workshop.employee.dto.PayDateResponse;
import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee/{employeeId}")
public class EmployeeController {
    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/goals")
    public List<String> goals(@PathVariable String employeeId) {
        return service.getGoals(employeeId);
    }

    @GetMapping("/learning-status")
    public LearningStatus learning(@PathVariable String employeeId) {
        return service.getLearning(employeeId);
    }

    @GetMapping("/next-pay-date")
    public PayDateResponse payDate(@PathVariable String employeeId) {
        return service.getNextPayDate(employeeId);
    }

    @GetMapping("/pto/balance")
    public PtoBalanceResponse balance(@PathVariable String employeeId) {
        return service.getPtoBalance(employeeId);
    }

    @PostMapping(value = "/pto/schedule", consumes = "application/json", produces = "application/json")
    public PtoScheduleResponse schedule(@PathVariable String employeeId,
                                        @Valid @RequestBody PtoScheduleRequest request) {
        return service.schedulePto(employeeId, request);
    }
}
