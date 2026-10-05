package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeeGoal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeGoalRepository extends JpaRepository<EmployeeGoal, Integer> {

    /**
     * {@code SELECT goal FROM employee_goals WHERE employee_id = :employeeId} followed by
     * the DataWeave {@code payload map $.goal} — projected straight to the goal strings.
     * No ORDER BY, matching the source query.
     */
    @Query("select g.goalDescription from EmployeeGoal g where g.employeeId = :employeeId")
    List<String> findGoalDescriptionsByEmployeeId(@Param("employeeId") String employeeId);
}
