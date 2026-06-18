package com.workshop.employee.dto;

import java.util.List;

public class LearningStatusResponse {

    private String employeeId;
    private List<CourseStatusDto> courses;

    public LearningStatusResponse(String employeeId, List<CourseStatusDto> courses) {
        this.employeeId = employeeId;
        this.courses = courses;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public List<CourseStatusDto> getCourses() {
        return courses;
    }
}
