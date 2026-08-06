package com.workshop.employee.dto;

import java.time.LocalDate;

public record PayDateResponse(String employeeId, LocalDate nextPayDate, String payFrequency) {
}
