package com.workshop.employee.dto;

import jakarta.validation.constraints.NotNull;

public class PtoScheduleRequest {

    @NotNull
    private String startDate;

    @NotNull
    private String endDate;

    @NotNull
    private Double hours;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }
}
