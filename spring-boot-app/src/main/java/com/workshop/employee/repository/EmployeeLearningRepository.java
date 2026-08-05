package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeeLearning;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeLearningRepository extends JpaRepository<EmployeeLearning, Integer> {
    List<EmployeeLearning> findByEmployeeId(String employeeId);
}
