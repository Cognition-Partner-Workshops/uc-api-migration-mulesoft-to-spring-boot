package com.workshop.employee.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PtoScheduleRequest {

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Double hours;

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getHours() { return hours; }
    public void setHours(Double hours) { this.hours = hours; }
}
