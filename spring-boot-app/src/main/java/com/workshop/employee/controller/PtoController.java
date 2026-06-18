package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.service.PtoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/employee/{employeeId}/pto")
public class PtoController {

    private final PtoService ptoService;

    public PtoController(PtoService ptoService) {
        this.ptoService = ptoService;
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@PathVariable String employeeId) {
        Optional<PtoBalanceResponse> balance = ptoService.getBalance(employeeId);
        if (balance.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("Employee PTO record not found", "NOT_FOUND"));
        }
        return ResponseEntity.ok(balance.get());
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> schedulePto(@PathVariable String employeeId,
                                         @Valid @RequestBody PtoScheduleRequest request) {
        PtoScheduleResponse response = ptoService.schedulePto(employeeId, request);
        return ResponseEntity.ok(response);
    }
}
