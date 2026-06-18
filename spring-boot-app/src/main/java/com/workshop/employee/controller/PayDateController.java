package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

@RestController
@RequestMapping("/api/employee/{employeeId}")
public class PayDateController {

    @GetMapping("/next-pay-date")
    public ResponseEntity<?> getNextPayDate(@PathVariable String employeeId) {
        // Calculate next bi-weekly Friday as the next pay date
        LocalDate today = LocalDate.now();
        LocalDate nextPayDate = today.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        return ResponseEntity.ok(Map.of(
                "employeeId", employeeId,
                "nextPayDate", nextPayDate.toString(),
                "payFrequency", "bi-weekly"
        ));
    }
}
