package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeePto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeePtoRepository extends JpaRepository<EmployeePto, Integer> {
    Optional<EmployeePto> findByEmployeeId(String employeeId);
}
