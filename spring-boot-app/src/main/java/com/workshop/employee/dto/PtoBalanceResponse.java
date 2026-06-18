package com.workshop.employee.dto;

public class PtoBalanceResponse {

    private String employeeId;
    private double balance;
    private double used;
    private double total;

    public PtoBalanceResponse() {
    }

    public PtoBalanceResponse(String employeeId, double balance, double used, double total) {
        this.employeeId = employeeId;
        this.balance = balance;
        this.used = used;
        this.total = total;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getUsed() {
        return used;
    }

    public void setUsed(double used) {
        this.used = used;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
