package com.workshop.employee.dto;

public class PtoBalanceResponse {

    private String employeeId;
    private Double balance;
    private Double used;
    private Double total;

    public PtoBalanceResponse(String employeeId, Double balance, Double used, Double total) {
        this.employeeId = employeeId;
        this.balance = balance;
        this.used = used;
        this.total = total;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public Double getBalance() {
        return balance;
    }

    public Double getUsed() {
        return used;
    }

    public Double getTotal() {
        return total;
    }
}
