package com.workshop.employee.dto;

public class CourseStatusDto {

    private String courseName;
    private String status;
    private Integer progress;

    public CourseStatusDto(String courseName, String status, Integer progress) {
        this.courseName = courseName;
        this.status = status;
        this.progress = progress;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getStatus() {
        return status;
    }

    public Integer getProgress() {
        return progress;
    }
}
