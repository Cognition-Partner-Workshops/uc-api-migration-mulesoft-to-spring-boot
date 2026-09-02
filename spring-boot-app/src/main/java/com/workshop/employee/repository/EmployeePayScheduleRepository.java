package com.workshop.employee.repository;

import com.workshop.employee.model.EmployeePaySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeePayScheduleRepository extends JpaRepository<EmployeePaySchedule, Integer> {
    Optional<EmployeePaySchedule> findByEmployeeId(String employeeId);
}
