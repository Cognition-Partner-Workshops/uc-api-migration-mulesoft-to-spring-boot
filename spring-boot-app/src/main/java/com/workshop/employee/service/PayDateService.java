package com.workshop.employee.service;

import com.workshop.employee.dto.PayDateResponse;
import com.workshop.employee.model.EmployeePto;
import com.workshop.employee.repository.EmployeePtoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Retrieves next pay date for an employee.
 *
 * MuleSoft source divergence: the MuleSoft flow returns a {@code status: "success"}
 * field in the 200 response, but the OpenAPI contract specifies {@code payFrequency}
 * instead. This implementation follows the OpenAPI contract (source of truth).
 */
@Service
public class PayDateService {

    private final EmployeePtoRepository employeePtoRepository;

    public PayDateService(EmployeePtoRepository employeePtoRepository) {
        this.employeePtoRepository = employeePtoRepository;
    }

    public Optional<PayDateResponse> getNextPayDate(String employeeId) {
        Optional<EmployeePto> pto = employeePtoRepository.findByEmployeeId(employeeId);
        if (pto.isEmpty() || pto.get().getNextPayDate() == null) {
            return Optional.empty();
        }

        EmployeePto record = pto.get();
        return Optional.of(new PayDateResponse(
                employeeId,
                record.getNextPayDate().toString(),
                record.getPayFrequency() != null ? record.getPayFrequency() : "BIWEEKLY"
        ));
    }
}
