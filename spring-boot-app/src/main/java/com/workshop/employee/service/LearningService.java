package com.workshop.employee.service;

import com.workshop.employee.dto.CourseStatusDto;
import com.workshop.employee.dto.LearningStatusResponse;
import com.workshop.employee.model.EmployeeLearning;
import com.workshop.employee.repository.EmployeeLearningRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LearningService {

    private final EmployeeLearningRepository learningRepository;

    public LearningService(EmployeeLearningRepository learningRepository) {
        this.learningRepository = learningRepository;
    }

    /**
     * Maps DB columns to DTO fields per the OpenAPI contract:
     *   course_name -> courseName
     *   status -> status (NOT_STARTED, IN_PROGRESS, COMPLETED)
     *   progress -> progress (0-100)
     *
     * NOTE: MuleSoft source returns {learningStatus: [{course, status}]} but
     * the OpenAPI contract specifies {courses: [{courseName, status, progress}]}.
     * The contract is the source of truth.
     */
    public Optional<LearningStatusResponse> getLearningStatus(String employeeId) {
        List<EmployeeLearning> records = learningRepository.findByEmployeeId(employeeId);
        if (records.isEmpty()) {
            return Optional.empty();
        }

        List<CourseStatusDto> courses = records.stream()
                .map(r -> new CourseStatusDto(r.getCourseName(), r.getStatus(), r.getProgress()))
                .toList();

        return Optional.of(new LearningStatusResponse(employeeId, courses));
    }
}
