package com.workshop.employee.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_pto")
public class EmployeePto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "employee_id", nullable = false, length = 50)
    private String employeeId;
    @Column(name = "total_hours", nullable = false)
    private Double totalHours;
    @Column(name = "used_hours", nullable = false)
    private Double usedHours;
    @Column(name = "\"year\"", nullable = false)
    private Integer ptoYear;

    protected EmployeePto() {
    }

    public Integer getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public Double getTotalHours() { return totalHours; }
    public Double getUsedHours() { return usedHours; }
    public Integer getPtoYear() { return ptoYear; }
    public void setUsedHours(Double usedHours) { this.usedHours = usedHours; }
}
