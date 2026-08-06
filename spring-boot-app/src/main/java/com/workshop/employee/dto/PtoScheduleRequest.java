package com.workshop.employee.dto;

import java.time.LocalDate;

public record PtoScheduleRequest(LocalDate startDate, LocalDate endDate, Double hours) {
}
