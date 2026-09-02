package com.workshop.employee.controller;

import com.workshop.employee.dto.*;
import com.workshop.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/{employeeId}")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/goals")
    public List<String> goals(@PathVariable String employeeId) {
        return employeeService.goals(employeeId);
    }

    @GetMapping("/learning-status")
    public LearningStatusResponse learningStatus(@PathVariable String employeeId) {
        return employeeService.learningStatus(employeeId);
    }

    @GetMapping("/next-pay-date")
    public PayDateResponse nextPayDate(@PathVariable String employeeId) {
        return employeeService.nextPayDate(employeeId);
    }

    @GetMapping("/pto/balance")
    public PtoBalanceResponse ptoBalance(@PathVariable String employeeId) {
        return employeeService.ptoBalance(employeeId);
    }

    @PostMapping("/pto/schedule")
    public PtoScheduleResponse schedulePto(@PathVariable String employeeId,
                                           @Valid @RequestBody PtoScheduleRequest request) {
        return employeeService.schedulePto(employeeId, request);
    }
}
