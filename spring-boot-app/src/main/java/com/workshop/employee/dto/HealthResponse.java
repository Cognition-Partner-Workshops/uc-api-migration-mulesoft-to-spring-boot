package com.workshop.employee.dto;

public class HealthResponse {

    private String status;
    private String database;

    public HealthResponse(String status, String database) {
        this.status = status;
        this.database = database;
    }

    public String getStatus() {
        return status;
    }

    public String getDatabase() {
        return database;
    }
}
