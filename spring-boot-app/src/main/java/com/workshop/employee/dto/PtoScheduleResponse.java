package com.workshop.employee.dto;

import java.time.LocalDate;

public record PtoScheduleResponse(
        String message,
        String requestId,
        LocalDate startDate,
        LocalDate endDate,
        double hoursScheduled) {
}
