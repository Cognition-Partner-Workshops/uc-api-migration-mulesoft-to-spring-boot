package com.workshop.employee.service;

import com.workshop.employee.dto.*;
import com.workshop.employee.exception.BadRequestException;
import com.workshop.employee.exception.ResourceNotFoundException;
import com.workshop.employee.model.*;
import com.workshop.employee.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeGoalRepository goalRepository;
    private final EmployeeLearningRepository learningRepository;
    private final EmployeePtoRepository ptoRepository;
    private final PtoRequestRepository ptoRequestRepository;
    private final EmployeePayScheduleRepository payScheduleRepository;
    private final PayDateService payDateService;

    public EmployeeService(EmployeeGoalRepository goalRepository,
                           EmployeeLearningRepository learningRepository,
                           EmployeePtoRepository ptoRepository,
                           PtoRequestRepository ptoRequestRepository,
                           EmployeePayScheduleRepository payScheduleRepository,
                           PayDateService payDateService) {
        this.goalRepository = goalRepository;
        this.learningRepository = learningRepository;
        this.ptoRepository = ptoRepository;
        this.ptoRequestRepository = ptoRequestRepository;
        this.payScheduleRepository = payScheduleRepository;
        this.payDateService = payDateService;
    }

    public List<String> goals(String employeeId) {
        List<EmployeeGoal> goals = goalRepository.findByEmployeeIdOrderByIdAsc(employeeId);
        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No goals found for employee " + employeeId, "NOT_FOUND");
        }
        return goals.stream().map(EmployeeGoal::getGoalDescription).toList();
    }

    public LearningStatusResponse learningStatus(String employeeId) {
        List<EmployeeLearning> learning = learningRepository.findByEmployeeIdOrderByIdAsc(employeeId);
        if (learning.isEmpty()) {
            throw new ResourceNotFoundException("No learning records found for employee " + employeeId, "NOT_FOUND");
        }
        return new LearningStatusResponse(employeeId, learning.stream()
                .map(course -> new CourseStatus(course.getCourseName(), course.getStatus(), course.getProgress()))
                .toList());
    }

    public PayDateResponse nextPayDate(String employeeId) {
        EmployeePaySchedule schedule = payScheduleRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No pay schedule found for employee " + employeeId, "NOT_FOUND"));
        LocalDate nextDate = payDateService.computeNextPayDate(
                schedule.getAnchorPayDate(), schedule.getPayFrequency(), LocalDate.now());
        return new PayDateResponse(employeeId, nextDate, schedule.getPayFrequency());
    }

    public PtoBalanceResponse ptoBalance(String employeeId) {
        EmployeePto pto = ptoRepository.findFirstByEmployeeIdOrderByPtoYearDesc(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No PTO balance found for employee " + employeeId, "NOT_FOUND"));
        return new PtoBalanceResponse(employeeId, pto.getTotalHours() - pto.getUsedHours(),
                pto.getUsedHours(), pto.getTotalHours());
    }

    @Transactional
    public PtoScheduleResponse schedulePto(String employeeId, PtoScheduleRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new BadRequestException("endDate must not be before startDate", "INVALID_DATES");
        }
        EmployeePto pto = ptoRepository.findFirstByEmployeeIdOrderByPtoYearDesc(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No PTO balance found for employee " + employeeId, "NOT_FOUND"));
        double balance = pto.getTotalHours() - pto.getUsedHours();
        if (request.hours() > balance) {
            throw new BadRequestException("Insufficient PTO balance", "INSUFFICIENT_BALANCE");
        }

        PtoRequest saved = ptoRequestRepository.saveAndFlush(new PtoRequest(
                employeeId, request.startDate(), request.endDate(), request.hours(), "APPROVED"));
        pto.setUsedHours(pto.getUsedHours() + request.hours());
        ptoRepository.save(pto);
        return new PtoScheduleResponse("PTO scheduled successfully", "PTO-" + saved.getId(),
                request.startDate(), request.endDate(), request.hours());
    }
}
