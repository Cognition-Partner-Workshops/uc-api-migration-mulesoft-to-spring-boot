package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeePto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeePtoRepository extends JpaRepository<EmployeePto, Long> {
    Optional<EmployeePto> findByEmployeeId(String employeeId);
}
