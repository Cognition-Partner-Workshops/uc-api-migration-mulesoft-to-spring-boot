package com.workshop.employee.service;

import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.model.EmployeePto;
import com.workshop.employee.model.PtoRequest;
import com.workshop.employee.repository.EmployeePtoRepository;
import com.workshop.employee.repository.PtoRequestRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PtoService {

    private final EmployeePtoRepository employeePtoRepository;
    private final PtoRequestRepository ptoRequestRepository;

    public PtoService(EmployeePtoRepository employeePtoRepository,
                      PtoRequestRepository ptoRequestRepository) {
        this.employeePtoRepository = employeePtoRepository;
        this.ptoRequestRepository = ptoRequestRepository;
    }

    public Optional<PtoBalanceResponse> getBalance(String employeeId) {
        Optional<EmployeePto> pto = employeePtoRepository.findByEmployeeId(employeeId);
        return pto.map(p -> new PtoBalanceResponse(
                p.getEmployeeId(),
                p.getTotalHours() - p.getUsedHours(),
                p.getUsedHours(),
                p.getTotalHours()
        ));
    }

    /**
     * Schedules PTO by inserting into the pto_requests table.
     * Note: The MuleSoft source has a quirk where it upserts into employee_pto
     * using endDate as next_pay_date, conflating PTO scheduling with pay date tracking.
     * The OpenAPI contract defines a cleaner model with a separate pto_requests table.
     */
    public PtoScheduleResponse schedulePto(String employeeId, PtoScheduleRequest request) {
        PtoRequest ptoRequest = new PtoRequest();
        ptoRequest.setEmployeeId(employeeId);
        ptoRequest.setStartDate(request.getStartDate());
        ptoRequest.setEndDate(request.getEndDate());
        ptoRequest.setHours(request.getHours());
        ptoRequest.setStatus("APPROVED");

        PtoRequest saved = ptoRequestRepository.save(ptoRequest);

        return new PtoScheduleResponse(
                "PTO scheduled successfully",
                String.valueOf(saved.getId()),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getHours()
        );
    }
}
