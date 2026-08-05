package com.workshop.employee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_goals")
public class EmployeeGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "employee_id", nullable = false)
    private String employeeId;
    @Column(name = "goal_description", nullable = false)
    private String goalDescription;
    @Column(name = "target_date")
    private LocalDate targetDate;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected EmployeeGoal() {
    }

    public String getGoalDescription() {
        return goalDescription;
    }
}
