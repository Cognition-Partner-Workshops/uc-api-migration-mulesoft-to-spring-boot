package com.workshop.employee.dto;

import java.util.List;

public class LearningStatusResponse {

    private String employeeId;
    private List<CourseStatus> courses;

    public LearningStatusResponse() {
    }

    public LearningStatusResponse(String employeeId, List<CourseStatus> courses) {
        this.employeeId = employeeId;
        this.courses = courses;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public List<CourseStatus> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseStatus> courses) {
        this.courses = courses;
    }

    public static class CourseStatus {
        private String courseName;
        private String status;
        private int progress;

        public CourseStatus() {
        }

        public CourseStatus(String courseName, String status, int progress) {
            this.courseName = courseName;
            this.status = status;
            this.progress = progress;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }
    }
}
