package com.workshop.employee.dto;

public record PtoBalanceResponse(String employeeId, double balance, double used, double total) {}
