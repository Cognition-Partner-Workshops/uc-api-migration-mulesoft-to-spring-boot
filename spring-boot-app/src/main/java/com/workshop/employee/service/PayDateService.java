package com.workshop.employee.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PayDateService {
    public LocalDate computeNextPayDate(LocalDate anchor, String frequency, LocalDate today) {
        if (anchor == null || frequency == null || today == null) {
            throw new IllegalArgumentException("Pay date inputs must not be null");
        }
        return switch (frequency) {
            case "WEEKLY" -> advance(anchor, today, 7);
            case "BI_WEEKLY" -> advance(anchor, today, 14);
            case "MONTHLY" -> advanceMonthly(anchor, today);
            case "SEMI_MONTHLY" -> advanceSemiMonthly(anchor, today);
            default -> throw new IllegalArgumentException("Unsupported pay frequency: " + frequency);
        };
    }

    private LocalDate advance(LocalDate candidate, LocalDate today, int days) {
        while (candidate.isBefore(today)) {
            candidate = candidate.plusDays(days);
        }
        return candidate;
    }

    private LocalDate advanceMonthly(LocalDate candidate, LocalDate today) {
        while (candidate.isBefore(today)) {
            candidate = candidate.plusMonths(1);
        }
        return candidate;
    }

    private LocalDate advanceSemiMonthly(LocalDate candidate, LocalDate today) {
        candidate = nextSemiMonthlyDate(candidate);
        while (candidate.isBefore(today)) {
            candidate = nextSemiMonthlyDate(candidate.plusDays(1));
        }
        return candidate;
    }

    private LocalDate nextSemiMonthlyDate(LocalDate date) {
        int lastDay = date.lengthOfMonth();
        if (date.getDayOfMonth() < 15) {
            return date.withDayOfMonth(15);
        }
        if (date.getDayOfMonth() < lastDay) {
            return date.withDayOfMonth(lastDay);
        }
        return date.plusMonths(1).withDayOfMonth(15);
    }
}
