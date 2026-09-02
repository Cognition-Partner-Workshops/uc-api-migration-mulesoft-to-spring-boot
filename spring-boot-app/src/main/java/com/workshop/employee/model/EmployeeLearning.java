package com.workshop.employee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_learning")
public class EmployeeLearning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "employee_id", nullable = false, length = 50)
    private String employeeId;
    @Column(name = "course_name", nullable = false)
    private String courseName;
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    private Integer progress;
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected EmployeeLearning() {
    }

    public Integer getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getCourseName() { return courseName; }
    public String getStatus() { return status; }
    public Integer getProgress() { return progress; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
