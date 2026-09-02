package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeeGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeGoalRepository extends JpaRepository<EmployeeGoal, Integer> {
    List<EmployeeGoal> findByEmployeeIdOrderByIdAsc(String employeeId);
}
