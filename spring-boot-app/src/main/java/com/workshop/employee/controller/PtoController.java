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
@RequestMapping("/api/employee/{employeeId}/pto")
public class PtoController {

    private final PtoService ptoService;

    public PtoController(PtoService ptoService) {
        this.ptoService = ptoService;
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@PathVariable String employeeId) {
        Optional<PtoBalanceResponse> result = ptoService.getBalance(employeeId);
        if (result.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse(
                            "Employee PTO record not found for " + employeeId,
                            "PTO_NOT_FOUND"));
        }
        return ResponseEntity.ok(result.get());
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> schedulePto(@PathVariable String employeeId,
                                         @RequestBody PtoScheduleRequest request) {
        PtoScheduleResponse response = ptoService.schedulePto(employeeId, request);
        return ResponseEntity.ok(response);
    }
}
