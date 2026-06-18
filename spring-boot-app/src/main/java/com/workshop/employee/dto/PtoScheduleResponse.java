package com.workshop.employee.dto;

import java.time.LocalDate;

public class PtoScheduleResponse {

    private String message;
    private String requestId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double hoursScheduled;

    public PtoScheduleResponse(String message, String requestId, LocalDate startDate,
                               LocalDate endDate, double hoursScheduled) {
        this.message = message;
        this.requestId = requestId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hoursScheduled = hoursScheduled;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public double getHoursScheduled() { return hoursScheduled; }
    public void setHoursScheduled(double hoursScheduled) { this.hoursScheduled = hoursScheduled; }
}
