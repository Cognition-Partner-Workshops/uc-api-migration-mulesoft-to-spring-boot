package com.workshop.employee.exception;

public class GoalsNotFoundException extends RuntimeException {

    public GoalsNotFoundException(String employeeId) {
        super("No goals found for employee " + employeeId);
    }
}
