package com.workshop.employee.service;

import com.workshop.employee.dto.PayDateResponse;
import com.workshop.employee.model.EmployeePto;
import com.workshop.employee.repository.EmployeePtoRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
public class PayDateService {

    private final EmployeePtoRepository ptoRepository;

    public PayDateService(EmployeePtoRepository ptoRepository) {
        this.ptoRepository = ptoRepository;
    }

    public Optional<PayDateResponse> getNextPayDate(String employeeId) {
        Optional<EmployeePto> pto = ptoRepository.findByEmployeeIdAndYear(employeeId, Year.now().getValue());
        if (pto.isEmpty()) {
            return Optional.empty();
        }

        LocalDate nextPayDate = calculateNextBiweeklyPayDate();
        return Optional.of(new PayDateResponse(employeeId, nextPayDate.toString(), "BIWEEKLY"));
    }

    private LocalDate calculateNextBiweeklyPayDate() {
        LocalDate today = LocalDate.now();
        LocalDate nextFriday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        int weekOfYear = nextFriday.getDayOfYear() / 7;
        if (weekOfYear % 2 != 0) {
            nextFriday = nextFriday.plusWeeks(1);
        }
        return nextFriday;
    }
}
