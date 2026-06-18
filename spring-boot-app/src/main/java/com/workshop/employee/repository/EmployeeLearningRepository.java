package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeeLearning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeLearningRepository extends JpaRepository<EmployeeLearning, Integer> {

    List<EmployeeLearning> findByEmployeeId(String employeeId);
}
