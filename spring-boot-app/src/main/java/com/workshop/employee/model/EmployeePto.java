package com.workshop.employee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee_pto")
public class EmployeePto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "employee_id", nullable = false)
    private String employeeId;
    @Column(name = "total_hours", nullable = false)
    private double totalHours;
    @Column(name = "used_hours", nullable = false)
    private double usedHours;
    @Column(nullable = false)
    private int year;

    protected EmployeePto() {
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getUsedHours() {
        return usedHours;
    }
}
