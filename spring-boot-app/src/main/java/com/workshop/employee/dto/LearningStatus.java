package com.workshop.employee.dto;

import java.util.List;

public record LearningStatus(String employeeId, List<CourseStatus> courses) {}
