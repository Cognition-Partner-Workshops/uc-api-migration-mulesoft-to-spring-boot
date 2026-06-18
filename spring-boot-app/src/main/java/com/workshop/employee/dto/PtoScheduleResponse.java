package com.workshop.employee.dto;

public class PtoScheduleResponse {

    private String message;
    private String requestId;
    private String startDate;
    private String endDate;
    private Double hoursScheduled;

    public PtoScheduleResponse(String message, String requestId, String startDate, String endDate, Double hoursScheduled) {
        this.message = message;
        this.requestId = requestId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hoursScheduled = hoursScheduled;
    }

    public String getMessage() {
        return message;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Double getHoursScheduled() {
        return hoursScheduled;
    }
}
