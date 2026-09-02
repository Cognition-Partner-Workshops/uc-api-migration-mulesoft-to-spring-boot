package com.workshop.employee.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_goals")
public class EmployeeGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "employee_id", nullable = false, length = 50)
    private String employeeId;
    @Column(name = "goal_description", nullable = false, columnDefinition = "TEXT")
    private String goalDescription;
    @Column(name = "target_date")
    private LocalDate targetDate;
    @Column(name = "status", length = 20)
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected EmployeeGoal() {
    }

    public Integer getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getGoalDescription() { return goalDescription; }
    public LocalDate getTargetDate() { return targetDate; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
