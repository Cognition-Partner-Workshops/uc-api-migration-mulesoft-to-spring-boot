package com.workshop.employee;

import com.workshop.employee.service.PayDateService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PayDateServiceTest {
    private final PayDateService service = new PayDateService();
    private final LocalDate today = LocalDate.of(2025, 1, 20);

    @Test
    void weekly() {
        assertEquals(LocalDate.of(2025, 1, 24),
                service.computeNextPayDate(LocalDate.of(2025, 1, 3), "WEEKLY", today));
    }

    @Test
    void biWeekly() {
        assertEquals(LocalDate.of(2025, 1, 31),
                service.computeNextPayDate(LocalDate.of(2025, 1, 3), "BI_WEEKLY", today));
    }

    @Test
    void monthly() {
        assertEquals(LocalDate.of(2025, 2, 3),
                service.computeNextPayDate(LocalDate.of(2025, 1, 3), "MONTHLY", today));
    }

    @Test
    void semiMonthly() {
        assertEquals(LocalDate.of(2025, 1, 31),
                service.computeNextPayDate(LocalDate.of(2025, 1, 3), "SEMI_MONTHLY", today));
    }

    @Test
    void everyResultIsTodayOrLater() {
        for (String frequency : new String[]{"WEEKLY", "BI_WEEKLY", "MONTHLY", "SEMI_MONTHLY"}) {
            assertFalse(service.computeNextPayDate(LocalDate.of(2024, 1, 3), frequency, today).isBefore(today));
        }
    }
}
