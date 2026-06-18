package com.workshop.employee.dto;

public class PayDateResponse {

    private String employeeId;
    private String nextPayDate;
    private String payFrequency;

    public PayDateResponse() {
    }

    public PayDateResponse(String employeeId, String nextPayDate, String payFrequency) {
        this.employeeId = employeeId;
        this.nextPayDate = nextPayDate;
        this.payFrequency = payFrequency;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getNextPayDate() {
        return nextPayDate;
    }

    public void setNextPayDate(String nextPayDate) {
        this.nextPayDate = nextPayDate;
    }

    public String getPayFrequency() {
        return payFrequency;
    }

    public void setPayFrequency(String payFrequency) {
        this.payFrequency = payFrequency;
    }
}
