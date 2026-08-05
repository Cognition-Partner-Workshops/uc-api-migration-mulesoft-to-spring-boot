package com.workshop.employee.service;

import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.exception.BadRequestException;
import com.workshop.employee.model.EmployeePto;
import com.workshop.employee.repository.EmployeeGoalRepository;
import com.workshop.employee.repository.EmployeeLearningRepository;
import com.workshop.employee.repository.EmployeePtoRepository;
import com.workshop.employee.repository.PtoRequestRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {
    private final EmployeePtoRepository pto = mock(EmployeePtoRepository.class);
    private final PtoRequestRepository requests = mock(PtoRequestRepository.class);
    private final EmployeeService service = new EmployeeService(mock(EmployeeGoalRepository.class),
        mock(EmployeeLearningRepository.class), pto, requests,
        Clock.fixed(java.time.Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void balanceIsTotalMinusUsed() {
        EmployeePto value = mock(EmployeePto.class);
        when(value.getTotalHours()).thenReturn(160.0);
        when(value.getUsedHours()).thenReturn(40.0);
        when(pto.findByEmployeeId("EMP001")).thenReturn(Optional.of(value));
        var result = service.getPtoBalance("EMP001");
        assertThat(result.balance()).isEqualTo(120.0);
        assertThat(result.used()).isEqualTo(40.0);
    }

    @Test
    void insufficientBalanceIsRejectedWithoutSaving() {
        EmployeePto value = mock(EmployeePto.class);
        when(value.getTotalHours()).thenReturn(80.0);
        when(value.getUsedHours()).thenReturn(40.0);
        when(pto.findByEmployeeId("EMP001")).thenReturn(Optional.of(value));
        var request = new PtoScheduleRequest(LocalDate.of(2025, 12, 1),
            LocalDate.of(2025, 12, 2), 41.0);
        assertThatThrownBy(() -> service.schedulePto("EMP001", request))
            .isInstanceOf(BadRequestException.class).hasMessage("Insufficient PTO balance");
        verifyNoInteractions(requests);
    }

    @Test
    void invalidDateRangeIsRejected() {
        var request = new PtoScheduleRequest(LocalDate.of(2025, 12, 2),
            LocalDate.of(2025, 12, 1), 8.0);
        assertThatThrownBy(() -> service.schedulePto("EMP001", request))
            .isInstanceOf(BadRequestException.class);
        verifyNoInteractions(pto);
    }

    @Test
    void nextPayDateUsesBiweeklyFridayAnchor() {
        when(pto.findByEmployeeId("EMP001")).thenReturn(Optional.of(mock(EmployeePto.class)));
        var result = service.getNextPayDate("EMP001");
        assertThat(result.employeeId()).isEqualTo("EMP001");
        assertThat(result.payFrequency()).isEqualTo("BIWEEKLY");
        assertThat(result.nextPayDate()).isEqualTo("2025-01-03");
    }
}
