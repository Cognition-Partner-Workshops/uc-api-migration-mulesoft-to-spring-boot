package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeeLearning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeLearningRepository extends JpaRepository<EmployeeLearning, Integer> {

    List<EmployeeLearning> findByEmployeeId(String employeeId);
}
