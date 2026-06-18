package com.workshop.employee.service;

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

    public Optional<LearningStatusResponse> getLearningStatus(String employeeId) {
        List<EmployeeLearning> records = learningRepository.findByEmployeeId(employeeId);
        if (records.isEmpty()) {
            return Optional.empty();
        }

        List<LearningStatusResponse.CourseStatus> courses = records.stream()
                .map(r -> new LearningStatusResponse.CourseStatus(
                        r.getCourseName(), r.getStatus(), r.getProgress()))
                .toList();

        return Optional.of(new LearningStatusResponse(employeeId, courses));
    }
}
