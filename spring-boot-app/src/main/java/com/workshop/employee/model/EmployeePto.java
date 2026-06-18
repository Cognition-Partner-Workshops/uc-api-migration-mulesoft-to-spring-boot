package com.workshop.employee.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_pto")
public class EmployeePto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "total_hours", nullable = false)
    private Double totalHours;

    @Column(name = "used_hours", nullable = false)
    private Double usedHours;

    @Column(name = "\"year\"", nullable = false)
    private Integer year;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public Double getTotalHours() { return totalHours; }
    public void setTotalHours(Double totalHours) { this.totalHours = totalHours; }

    public Double getUsedHours() { return usedHours; }
    public void setUsedHours(Double usedHours) { this.usedHours = usedHours; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
