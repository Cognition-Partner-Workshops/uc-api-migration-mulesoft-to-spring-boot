package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeeGoal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeGoalRepository extends JpaRepository<EmployeeGoal, Integer> {
    List<EmployeeGoal> findByEmployeeId(String employeeId);
}
