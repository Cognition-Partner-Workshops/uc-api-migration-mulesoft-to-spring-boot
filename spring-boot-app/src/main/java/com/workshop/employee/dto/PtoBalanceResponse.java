package com.workshop.employee.dto;

public record PtoBalanceResponse(String employeeId, Double balance, Double used, Double total) {
}
