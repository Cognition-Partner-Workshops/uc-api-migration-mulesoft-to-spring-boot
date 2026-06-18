package com.workshop.employee.service;

import com.workshop.employee.model.EmployeeGoal;
import com.workshop.employee.repository.EmployeeGoalRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmployeeGoalService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeGoalService.class);

    private final EmployeeGoalRepository employeeGoalRepository;

    public EmployeeGoalService(EmployeeGoalRepository employeeGoalRepository) {
        this.employeeGoalRepository = employeeGoalRepository;
    }

    public List<String> getGoals(String employeeId) {
        log.info("Retrieving goals for employee: {}", employeeId);
        List<EmployeeGoal> goals = employeeGoalRepository.findByEmployeeId(employeeId);
        return goals.stream()
                .map(EmployeeGoal::getGoalDescription)
                .toList();
    }
}
