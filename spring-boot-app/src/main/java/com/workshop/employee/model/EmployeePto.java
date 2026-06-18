package com.workshop.employee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

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

    @Column(name = "next_pay_date")
    private LocalDate nextPayDate;

    @Column(name = "pay_frequency")
    private String payFrequency;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDate getNextPayDate() {
        return nextPayDate;
    }

    public void setNextPayDate(LocalDate nextPayDate) {
        this.nextPayDate = nextPayDate;
    }

    public String getPayFrequency() {
        return payFrequency;
    }

    public void setPayFrequency(String payFrequency) {
        this.payFrequency = payFrequency;
    }
}
