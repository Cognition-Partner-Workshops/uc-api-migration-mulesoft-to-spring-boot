package com.workshop.employee.exception;

public class GoalsNotFoundException extends RuntimeException {

    private final String employeeId;

    public GoalsNotFoundException(String employeeId) {
        super("No goals found for employee " + employeeId);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }
}
