package com.workshop.employee.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee_pay_schedule")
public class EmployeePaySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "employee_id", nullable = false, unique = true, length = 50)
    private String employeeId;
    @Column(name = "pay_frequency", nullable = false, length = 20)
    private String payFrequency;
    @Column(name = "anchor_pay_date", nullable = false)
    private LocalDate anchorPayDate;

    protected EmployeePaySchedule() {
    }

    public Integer getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getPayFrequency() { return payFrequency; }
    public LocalDate getAnchorPayDate() { return anchorPayDate; }
}
