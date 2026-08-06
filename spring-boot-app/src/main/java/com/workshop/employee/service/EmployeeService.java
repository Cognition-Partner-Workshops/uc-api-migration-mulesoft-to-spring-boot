package com.workshop.employee.service;

import com.workshop.employee.dto.CourseStatus;
import com.workshop.employee.dto.LearningStatusResponse;
import com.workshop.employee.dto.PayDateResponse;
import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.exception.InvalidRequestException;
import com.workshop.employee.exception.NotFoundException;
import com.workshop.employee.model.EmployeeGoal;
import com.workshop.employee.model.EmployeePto;
import com.workshop.employee.model.PtoRequest;
import com.workshop.employee.repository.EmployeeGoalRepository;
import com.workshop.employee.repository.EmployeeLearningRepository;
import com.workshop.employee.repository.EmployeePtoRepository;
import com.workshop.employee.repository.PtoRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class EmployeeService {

    private static final String PAY_FREQUENCY = "SEMI_MONTHLY";

    private final EmployeeGoalRepository goalRepository;
    private final EmployeeLearningRepository learningRepository;
    private final EmployeePtoRepository ptoRepository;
    private final PtoRequestRepository ptoRequestRepository;

    public EmployeeService(EmployeeGoalRepository goalRepository,
                           EmployeeLearningRepository learningRepository,
                           EmployeePtoRepository ptoRepository,
                           PtoRequestRepository ptoRequestRepository) {
        this.goalRepository = goalRepository;
        this.learningRepository = learningRepository;
        this.ptoRepository = ptoRepository;
        this.ptoRequestRepository = ptoRequestRepository;
    }

    public List<String> getGoals(String employeeId) {
        List<EmployeeGoal> goals = goalRepository.findByEmployeeId(employeeId);
        if (goals.isEmpty()) {
            throw new NotFoundException("No goals found for employee " + employeeId);
        }
        return goals.stream().map(EmployeeGoal::getGoalDescription).toList();
    }

    public LearningStatusResponse getLearningStatus(String employeeId) {
        List<CourseStatus> courses = learningRepository.findByEmployeeId(employeeId).stream()
                .map(l -> new CourseStatus(l.getCourseName(), l.getStatus(), l.getProgress()))
                .toList();
        if (courses.isEmpty()) {
            throw new NotFoundException("Learning records not found for employee " + employeeId);
        }
        return new LearningStatusResponse(employeeId, courses);
    }

    public PayDateResponse getNextPayDate(String employeeId) {
        findPto(employeeId);
        return new PayDateResponse(employeeId, computeNextPayDate(LocalDate.now()), PAY_FREQUENCY);
    }

    public PtoBalanceResponse getPtoBalance(String employeeId) {
        EmployeePto pto = findPto(employeeId);
        double balance = pto.getTotalHours() - pto.getUsedHours();
        return new PtoBalanceResponse(employeeId, balance, pto.getUsedHours(), pto.getTotalHours());
    }

    @Transactional
    public PtoScheduleResponse schedulePto(String employeeId, PtoScheduleRequest request) {
        if (request.startDate() == null || request.endDate() == null || request.hours() == null) {
            throw new InvalidRequestException("startDate, endDate and hours are required");
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new InvalidRequestException("endDate must not be before startDate");
        }
        if (request.hours() <= 0) {
            throw new InvalidRequestException("hours must be greater than zero");
        }

        EmployeePto pto = findPto(employeeId);
        double available = pto.getTotalHours() - pto.getUsedHours();
        if (request.hours() > available) {
            throw new InvalidRequestException(
                    "Insufficient PTO balance: requested " + request.hours()
                            + " hours but only " + available + " available");
        }

        pto.setUsedHours(pto.getUsedHours() + request.hours());
        ptoRepository.save(pto);

        PtoRequest ptoRequest = new PtoRequest();
        ptoRequest.setEmployeeId(employeeId);
        ptoRequest.setStartDate(request.startDate());
        ptoRequest.setEndDate(request.endDate());
        ptoRequest.setHours(request.hours());
        ptoRequest.setStatus("APPROVED");
        PtoRequest saved = ptoRequestRepository.save(ptoRequest);

        return new PtoScheduleResponse(
                "PTO scheduled successfully",
                String.valueOf(saved.getId()),
                request.startDate(),
                request.endDate(),
                request.hours());
    }

    private EmployeePto findPto(String employeeId) {
        return ptoRepository.findFirstByEmployeeIdOrderByYearDesc(employeeId)
                .orElseThrow(() -> new NotFoundException(
                        "Employee PTO record not found for employee " + employeeId));
    }

    static LocalDate computeNextPayDate(LocalDate today) {
        if (today.getDayOfMonth() < 15) {
            return today.withDayOfMonth(15);
        }
        return today.with(TemporalAdjusters.lastDayOfMonth());
    }
}
