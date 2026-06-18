package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeeGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeGoalRepository extends JpaRepository<EmployeeGoal, Integer> {

    List<EmployeeGoal> findByEmployeeId(String employeeId);
}
