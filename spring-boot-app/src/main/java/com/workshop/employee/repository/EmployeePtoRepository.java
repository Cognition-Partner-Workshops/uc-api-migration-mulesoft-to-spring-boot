package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeePto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeePtoRepository extends JpaRepository<EmployeePto, Integer> {

    Optional<EmployeePto> findFirstByEmployeeIdOrderByYearDesc(String employeeId);
}
