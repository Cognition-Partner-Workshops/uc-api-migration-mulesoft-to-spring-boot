package com.workshop.employee.dto;

public class PtoScheduleResponse {

    private String message;
    private String requestId;
    private String startDate;
    private String endDate;
    private double hoursScheduled;

    public PtoScheduleResponse() {
    }

    public PtoScheduleResponse(String message, String requestId, String startDate, String endDate, double hoursScheduled) {
        this.message = message;
        this.requestId = requestId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hoursScheduled = hoursScheduled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

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

    public double getHoursScheduled() {
        return hoursScheduled;
    }

    public void setHoursScheduled(double hoursScheduled) {
        this.hoursScheduled = hoursScheduled;
    }
}
