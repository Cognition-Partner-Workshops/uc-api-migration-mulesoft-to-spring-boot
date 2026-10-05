package com.workshop.employee.controller;

import com.workshop.employee.service.EmployeeGoalService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring equivalent of the APIKit-routed flow {@code get:\employee\(employeeId)\goals}.
 * Token validation is applied by {@link com.workshop.employee.config.TokenValidationInterceptor}.
 */
@RestController
@RequestMapping("/api/employee/{employeeId}")
public class EmployeeGoalController {

    private final EmployeeGoalService employeeGoalService;

    public EmployeeGoalController(EmployeeGoalService employeeGoalService) {
        this.employeeGoalService = employeeGoalService;
    }

    /** 200: bare JSON array of goal strings ({@code payload map $.goal}). */
    @GetMapping(value = "/goals", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getGoals(@PathVariable String employeeId) {
        return employeeGoalService.getGoals(employeeId);
    }
}
