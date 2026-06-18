package com.workshop.employee.service;

import com.workshop.employee.exception.GoalsNotFoundException;
import com.workshop.employee.model.EmployeeGoal;
import com.workshop.employee.repository.EmployeeGoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    private final EmployeeGoalRepository employeeGoalRepository;

    public GoalService(EmployeeGoalRepository employeeGoalRepository) {
        this.employeeGoalRepository = employeeGoalRepository;
    }

    /**
     * Retrieves goal descriptions for an employee.
     * Mirrors MuleSoft: SELECT goal FROM employee_goals WHERE employee_id = :employeeId
     * then transforms via: payload map $.goal
     *
     * DIVERGENCE: The MuleSoft source returns HTTP 200 with {"message": "No goals found..."}
     * when no goals exist. The OpenAPI contract specifies HTTP 404. We follow the contract
     * (source of truth) and return 404, flagging this divergence.
     */
    public List<String> getGoalsForEmployee(String employeeId) {
        List<EmployeeGoal> goals = employeeGoalRepository.findByEmployeeId(employeeId);
        if (goals.isEmpty()) {
            throw new GoalsNotFoundException(employeeId);
        }
        return goals.stream()
                .map(EmployeeGoal::getGoalDescription)
                .toList();
    }
}
