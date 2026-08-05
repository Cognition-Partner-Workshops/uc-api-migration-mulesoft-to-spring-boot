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
@Table(name = "pto_requests")
public class PtoRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "employee_id", nullable = false)
    private String employeeId;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(nullable = false)
    private double hours;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected PtoRequest() {
    }

    public PtoRequest(String employeeId, LocalDate startDate, LocalDate endDate, double hours) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hours = hours;
    }

    public Integer getId() {
        return id;
    }
}
