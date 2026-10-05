package com.workshop.employee.service;

import com.workshop.employee.exception.GoalsNotFoundException;
import com.workshop.employee.repository.EmployeeGoalRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Spring equivalent of the {@code get:\employee\(employeeId)\goals} flow. */
@Service
public class EmployeeGoalService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeGoalService.class);

    private final EmployeeGoalRepository employeeGoalRepository;

    public EmployeeGoalService(EmployeeGoalRepository employeeGoalRepository) {
        this.employeeGoalRepository = employeeGoalRepository;
    }

    /**
     * Returns the goal strings for the employee.
     *
     * <p>MIGRATION NOTE — on an empty result the Mule flow builds
     * {@code {"message": "No goals found for employee <id>"}} but sets {@code httpStatus = 200}.
     * The RAML and {@code contracts/openapi.yaml} both declare that body as a 404, and the
     * contract is the source of truth, so this implementation returns 404. Flagged in the PR.
     */
    @Transactional(readOnly = true)
    public List<String> getGoals(String employeeId) {
        log.info("Processing request for employee goals: {}", employeeId);
        List<String> goals = employeeGoalRepository.findGoalDescriptionsByEmployeeId(employeeId);
        if (goals.isEmpty()) {
            throw new GoalsNotFoundException(employeeId);
        }
        return goals;
    }
}
