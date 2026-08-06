package com.workshop.employee.controller;

import com.workshop.employee.dto.LearningStatusResponse;
import com.workshop.employee.dto.PayDateResponse;
import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee/{employeeId}")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/goals")
    public List<String> getGoals(@PathVariable String employeeId) {
        return employeeService.getGoals(employeeId);
    }

    @GetMapping("/learning-status")
    public LearningStatusResponse getLearningStatus(@PathVariable String employeeId) {
        return employeeService.getLearningStatus(employeeId);
    }

    @GetMapping("/next-pay-date")
    public PayDateResponse getNextPayDate(@PathVariable String employeeId) {
        return employeeService.getNextPayDate(employeeId);
    }

    @GetMapping("/pto/balance")
    public PtoBalanceResponse getPtoBalance(@PathVariable String employeeId) {
        return employeeService.getPtoBalance(employeeId);
    }

    @PostMapping("/pto/schedule")
    public PtoScheduleResponse schedulePto(@PathVariable String employeeId,
                                           @RequestBody PtoScheduleRequest request) {
        return employeeService.schedulePto(employeeId, request);
    }
}
