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
@RequestMapping("/api/employee/{employeeId}")
public class PayDateController {

    private final PayDateService payDateService;

    public PayDateController(PayDateService payDateService) {
        this.payDateService = payDateService;
    }

    /**
     * GET /api/employee/{employeeId}/next-pay-date
     *
     * MuleSoft source divergence: the MuleSoft flow returns {@code "status": "success"}
     * in the response body. The OpenAPI contract specifies {@code payFrequency} instead.
     * This implementation follows the OpenAPI contract.
     */
    @GetMapping("/next-pay-date")
    public ResponseEntity<?> getNextPayDate(@PathVariable String employeeId) {
        Optional<PayDateResponse> result = payDateService.getNextPayDate(employeeId);
        if (result.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse(
                            "Pay date not found for employee " + employeeId,
                            "PAY_DATE_NOT_FOUND"));
        }
        return ResponseEntity.ok(result.get());
    }
}
