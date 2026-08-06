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

    @Column(name = "year", nullable = false)
    private int year;

    public Integer getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public double getUsedHours() {
        return usedHours;
    }

    public void setUsedHours(double usedHours) {
        this.usedHours = usedHours;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
