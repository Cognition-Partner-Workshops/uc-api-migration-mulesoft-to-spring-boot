package com.workshop.employee.dto;

import java.util.List;

public record LearningStatusResponse(String employeeId, List<CourseStatus> courses) {
}
