package com.workshop.employee.service;

import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.model.EmployeePto;
import com.workshop.employee.model.PtoRequest;
import com.workshop.employee.repository.EmployeePtoRepository;
import com.workshop.employee.repository.PtoRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class PtoService {

    private final EmployeePtoRepository ptoRepository;
    private final PtoRequestRepository ptoRequestRepository;

    public PtoService(EmployeePtoRepository ptoRepository, PtoRequestRepository ptoRequestRepository) {
        this.ptoRepository = ptoRepository;
        this.ptoRequestRepository = ptoRequestRepository;
    }

    public Optional<PtoBalanceResponse> getBalance(String employeeId) {
        Optional<EmployeePto> pto = ptoRepository.findByEmployeeId(employeeId);
        if (pto.isEmpty()) {
            return Optional.empty();
        }
        EmployeePto record = pto.get();
        double balance = record.getTotalHours() - record.getUsedHours();
        return Optional.of(new PtoBalanceResponse(
                employeeId, balance, record.getUsedHours(), record.getTotalHours()));
    }

    public PtoScheduleResponse schedulePto(String employeeId, PtoScheduleRequest request) {
        PtoRequest ptoRequest = new PtoRequest();
        ptoRequest.setEmployeeId(employeeId);
        ptoRequest.setStartDate(LocalDate.parse(request.getStartDate()));
        ptoRequest.setEndDate(LocalDate.parse(request.getEndDate()));
        ptoRequest.setHours(request.getHours());
        ptoRequest.setStatus("APPROVED");
        ptoRequestRepository.save(ptoRequest);

        return new PtoScheduleResponse(
                "PTO scheduled successfully",
                UUID.randomUUID().toString(),
                request.getStartDate(),
                request.getEndDate(),
                request.getHours());
    }
}
