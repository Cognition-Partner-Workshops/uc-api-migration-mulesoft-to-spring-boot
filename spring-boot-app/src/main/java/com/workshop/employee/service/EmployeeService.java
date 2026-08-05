package com.workshop.employee.service;

import com.workshop.employee.dto.CourseStatus;
import com.workshop.employee.dto.LearningStatus;
import com.workshop.employee.dto.PayDateResponse;
import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.exception.BadRequestException;
import com.workshop.employee.exception.NotFoundException;
import com.workshop.employee.model.PtoRequest;
import com.workshop.employee.repository.EmployeeGoalRepository;
import com.workshop.employee.repository.EmployeeLearningRepository;
import com.workshop.employee.repository.EmployeePtoRepository;
import com.workshop.employee.repository.PtoRequestRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    private static final LocalDate PAY_ANCHOR = LocalDate.of(2025, 1, 3);
    private final EmployeeGoalRepository goals;
    private final EmployeeLearningRepository learning;
    private final EmployeePtoRepository pto;
    private final PtoRequestRepository requests;
    private final Clock clock;

    public EmployeeService(EmployeeGoalRepository goals, EmployeeLearningRepository learning,
                           EmployeePtoRepository pto, PtoRequestRepository requests, Clock clock) {
        this.goals = goals;
        this.learning = learning;
        this.pto = pto;
        this.requests = requests;
        this.clock = clock;
    }

    public List<String> getGoals(String employeeId) {
        List<String> result = goals.findByEmployeeId(employeeId).stream()
            .map(g -> g.getGoalDescription())
            .toList();
        if (result.isEmpty()) {
            throw new NotFoundException("No goals found for employee " + employeeId);
        }
        return result;
    }

    public LearningStatus getLearning(String employeeId) {
        List<CourseStatus> courses = learning.findByEmployeeId(employeeId).stream()
            .map(c -> new CourseStatus(c.getCourseName(), c.getStatus(), c.getProgress()))
            .toList();
        if (courses.isEmpty()) {
            throw new NotFoundException("No learning records found for employee " + employeeId);
        }
        return new LearningStatus(employeeId, courses);
    }

    public PayDateResponse getNextPayDate(String employeeId) {
        pto.findByEmployeeId(employeeId)
            .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));
        LocalDate today = LocalDate.now(clock);
        long days = ChronoUnit.DAYS.between(PAY_ANCHOR, today);
        long periods = Math.floorDiv(days, 14);
        LocalDate next = PAY_ANCHOR.plusDays(periods * 14);
        if (!next.isAfter(today)) {
            next = next.plusDays(14);
        }
        return new PayDateResponse(employeeId, next.toString(), "BIWEEKLY");
    }

    public PtoBalanceResponse getPtoBalance(String employeeId) {
        var value = pto.findByEmployeeId(employeeId)
            .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));
        return new PtoBalanceResponse(employeeId, value.getTotalHours() - value.getUsedHours(),
            value.getUsedHours(), value.getTotalHours());
    }

    @Transactional
    public PtoScheduleResponse schedulePto(String employeeId, PtoScheduleRequest request) {
        if (request == null || request.startDate() == null || request.endDate() == null
            || request.hours() == null) {
            throw new BadRequestException("Invalid PTO request");
        }
        if (request.startDate().isAfter(request.endDate())) {
            throw new BadRequestException("Start date must not be after end date");
        }
        if (request.hours() <= 0) {
            throw new BadRequestException("Hours must be greater than zero");
        }
        var balance = pto.findByEmployeeId(employeeId)
            .orElseThrow(() -> new BadRequestException("Employee has no PTO balance"));
        double available = balance.getTotalHours() - balance.getUsedHours();
        if (request.hours() > available) {
            throw new BadRequestException("Insufficient PTO balance");
        }
        PtoRequest saved = requests.save(new PtoRequest(employeeId, request.startDate(),
            request.endDate(), request.hours()));
        return new PtoScheduleResponse("PTO scheduled successfully", saved.getId().toString(),
            request.startDate(), request.endDate(), request.hours());
    }
}
