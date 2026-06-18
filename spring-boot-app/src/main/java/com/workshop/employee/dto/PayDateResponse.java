package com.workshop.employee.dto;

public class PayDateResponse {

    private String employeeId;
    private String nextPayDate;
    private String payFrequency;

    public PayDateResponse(String employeeId, String nextPayDate, String payFrequency) {
        this.employeeId = employeeId;
        this.nextPayDate = nextPayDate;
        this.payFrequency = payFrequency;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getNextPayDate() {
        return nextPayDate;
    }

    public String getPayFrequency() {
        return payFrequency;
    }
}
