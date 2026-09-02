package com.workshop.employee.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pto_requests")
public class PtoRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "employee_id", nullable = false, length = 50)
    private String employeeId;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(name = "hours", nullable = false)
    private Double hours;
    @Column(name = "status", length = 20)
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected PtoRequest() {
    }

    public PtoRequest(String employeeId, LocalDate startDate, LocalDate endDate, Double hours, String status) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hours = hours;
        this.status = status;
    }

    public Integer getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Double getHours() { return hours; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
