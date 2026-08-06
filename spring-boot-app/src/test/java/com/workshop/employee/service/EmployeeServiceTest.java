package com.workshop.employee.service;

import com.workshop.employee.dto.LearningStatusResponse;
import com.workshop.employee.dto.PayDateResponse;
import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.exception.InvalidRequestException;
import com.workshop.employee.exception.NotFoundException;
import com.workshop.employee.model.EmployeeGoal;
import com.workshop.employee.model.EmployeeLearning;
import com.workshop.employee.model.EmployeePto;
import com.workshop.employee.model.PtoRequest;
import com.workshop.employee.repository.EmployeeGoalRepository;
import com.workshop.employee.repository.EmployeeLearningRepository;
import com.workshop.employee.repository.EmployeePtoRepository;
import com.workshop.employee.repository.PtoRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeGoalRepository goalRepository;
    @Mock
    private EmployeeLearningRepository learningRepository;
    @Mock
    private EmployeePtoRepository ptoRepository;
    @Mock
    private PtoRequestRepository ptoRequestRepository;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(
                goalRepository, learningRepository, ptoRepository, ptoRequestRepository);
    }

    private static EmployeeGoal goal(String description) {
        EmployeeGoal g = new EmployeeGoal();
        g.setEmployeeId("EMP001");
        g.setGoalDescription(description);
        return g;
    }

    private static EmployeePto pto(double total, double used) {
        EmployeePto p = new EmployeePto();
        p.setEmployeeId("EMP001");
        p.setTotalHours(total);
        p.setUsedHours(used);
        p.setYear(2025);
        return p;
    }

    @Test
    void getGoalsReturnsDescriptions() {
        when(goalRepository.findByEmployeeId("EMP001"))
                .thenReturn(List.of(goal("Goal A"), goal("Goal B")));

        assertThat(employeeService.getGoals("EMP001")).containsExactly("Goal A", "Goal B");
    }

    @Test
    void getGoalsThrowsNotFoundWhenEmpty() {
        when(goalRepository.findByEmployeeId("UNKNOWN999")).thenReturn(List.of());

        assertThatThrownBy(() -> employeeService.getGoals("UNKNOWN999"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("UNKNOWN999");
    }

    @Test
    void getLearningStatusReturnsCourses() {
        EmployeeLearning l = new EmployeeLearning();
        l.setEmployeeId("EMP001");
        l.setCourseName("Spring Boot Fundamentals");
        l.setStatus("COMPLETED");
        l.setProgress(100);
        when(learningRepository.findByEmployeeId("EMP001")).thenReturn(List.of(l));

        LearningStatusResponse response = employeeService.getLearningStatus("EMP001");

        assertThat(response.employeeId()).isEqualTo("EMP001");
        assertThat(response.courses()).hasSize(1);
        assertThat(response.courses().get(0).courseName()).isEqualTo("Spring Boot Fundamentals");
        assertThat(response.courses().get(0).status()).isEqualTo("COMPLETED");
        assertThat(response.courses().get(0).progress()).isEqualTo(100);
    }

    @Test
    void getLearningStatusThrowsNotFoundWhenEmpty() {
        when(learningRepository.findByEmployeeId("UNKNOWN999")).thenReturn(List.of());

        assertThatThrownBy(() -> employeeService.getLearningStatus("UNKNOWN999"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getNextPayDateReturnsDateForKnownEmployee() {
        when(ptoRepository.findFirstByEmployeeIdOrderByYearDesc("EMP001"))
                .thenReturn(Optional.of(pto(160, 40)));

        PayDateResponse response = employeeService.getNextPayDate("EMP001");

        assertThat(response.employeeId()).isEqualTo("EMP001");
        assertThat(response.nextPayDate()).isAfterOrEqualTo(LocalDate.now());
    }

    @Test
    void getNextPayDateThrowsNotFoundForUnknownEmployee() {
        when(ptoRepository.findFirstByEmployeeIdOrderByYearDesc("UNKNOWN999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getNextPayDate("UNKNOWN999"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void computeNextPayDateReturnsMidMonthOrEndOfMonth() {
        assertThat(EmployeeService.computeNextPayDate(LocalDate.of(2025, 6, 5)))
                .isEqualTo(LocalDate.of(2025, 6, 15));
        assertThat(EmployeeService.computeNextPayDate(LocalDate.of(2025, 6, 20)))
                .isEqualTo(LocalDate.of(2025, 6, 30));
    }

    @Test
    void getPtoBalanceComputesAvailableHours() {
        when(ptoRepository.findFirstByEmployeeIdOrderByYearDesc("EMP001"))
                .thenReturn(Optional.of(pto(160, 40)));

        PtoBalanceResponse response = employeeService.getPtoBalance("EMP001");

        assertThat(response.balance()).isEqualTo(120.0);
        assertThat(response.used()).isEqualTo(40.0);
        assertThat(response.total()).isEqualTo(160.0);
    }

    @Test
    void schedulePtoDeductsBalanceAndSavesRequest() {
        when(ptoRepository.findFirstByEmployeeIdOrderByYearDesc("EMP001"))
                .thenReturn(Optional.of(pto(160, 40)));
        when(ptoRequestRepository.save(any(PtoRequest.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ptoRepository.save(any(EmployeePto.class))).thenAnswer(inv -> inv.getArgument(0));

        PtoScheduleRequest request = new PtoScheduleRequest(
                LocalDate.of(2025, 12, 22), LocalDate.of(2025, 12, 26), 40.0);

        PtoScheduleResponse response = employeeService.schedulePto("EMP001", request);

        assertThat(response.message()).isEqualTo("PTO scheduled successfully");
        assertThat(response.hoursScheduled()).isEqualTo(40.0);
        assertThat(response.startDate()).isEqualTo(LocalDate.of(2025, 12, 22));
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2025, 12, 26));
    }

    @Test
    void schedulePtoRejectsInsufficientBalance() {
        when(ptoRepository.findFirstByEmployeeIdOrderByYearDesc("EMP001"))
                .thenReturn(Optional.of(pto(160, 150)));

        PtoScheduleRequest request = new PtoScheduleRequest(
                LocalDate.of(2025, 12, 22), LocalDate.of(2025, 12, 26), 40.0);

        assertThatThrownBy(() -> employeeService.schedulePto("EMP001", request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Insufficient PTO balance");
    }

    @Test
    void schedulePtoRejectsInvalidDates() {
        PtoScheduleRequest request = new PtoScheduleRequest(
                LocalDate.of(2025, 12, 26), LocalDate.of(2025, 12, 22), 8.0);

        assertThatThrownBy(() -> employeeService.schedulePto("EMP001", request))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void schedulePtoRejectsMissingFields() {
        PtoScheduleRequest request = new PtoScheduleRequest(null, null, null);

        assertThatThrownBy(() -> employeeService.schedulePto("EMP001", request))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void schedulePtoRejectsNonPositiveHours() {
        PtoScheduleRequest request = new PtoScheduleRequest(
                LocalDate.of(2025, 12, 22), LocalDate.of(2025, 12, 26), 0.0);

        assertThatThrownBy(() -> employeeService.schedulePto("EMP001", request))
                .isInstanceOf(InvalidRequestException.class);
    }
}
