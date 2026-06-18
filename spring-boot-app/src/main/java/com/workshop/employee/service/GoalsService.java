package com.workshop.employee.service;

import com.workshop.employee.model.EmployeeGoal;
import com.workshop.employee.repository.EmployeeGoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalsService {

    private final EmployeeGoalRepository goalRepository;

    public GoalsService(EmployeeGoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<String> getGoals(String employeeId) {
        List<EmployeeGoal> goals = goalRepository.findByEmployeeId(employeeId);
        return goals.stream()
                .map(EmployeeGoal::getGoalDescription)
                .toList();
    }
}
