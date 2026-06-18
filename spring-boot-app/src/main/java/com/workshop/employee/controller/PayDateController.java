package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.dto.PayDateResponse;
import com.workshop.employee.service.PayDateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
public class PayDateController {

    private final PayDateService payDateService;

    public PayDateController(PayDateService payDateService) {
        this.payDateService = payDateService;
    }

    @GetMapping("/{employeeId}/next-pay-date")
    public ResponseEntity<?> getNextPayDate(@PathVariable String employeeId) {
        Optional<PayDateResponse> result = payDateService.getNextPayDate(employeeId);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.status(404)
                .body(new ErrorResponse("Employee not found: " + employeeId, "NOT_FOUND"));
    }
}
