package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.service.PtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
public class PtoController {

    private final PtoService ptoService;

    public PtoController(PtoService ptoService) {
        this.ptoService = ptoService;
    }

    @GetMapping("/{employeeId}/pto/balance")
    public ResponseEntity<?> getPtoBalance(@PathVariable String employeeId) {
        Optional<PtoBalanceResponse> result = ptoService.getBalance(employeeId);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.status(404)
                .body(new ErrorResponse("Employee not found: " + employeeId, "NOT_FOUND"));
    }

    @PostMapping("/{employeeId}/pto/schedule")
    public ResponseEntity<?> schedulePto(@PathVariable String employeeId, @RequestBody PtoScheduleRequest request) {
        Optional<PtoScheduleResponse> result = ptoService.schedulePto(employeeId, request);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.status(400)
                .body(new ErrorResponse("Insufficient PTO balance or employee not found", "BAD_REQUEST"));
    }
}
