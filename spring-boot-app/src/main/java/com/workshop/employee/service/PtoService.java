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
import java.time.Year;
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
        Optional<EmployeePto> pto = ptoRepository.findByEmployeeIdAndYear(employeeId, Year.now().getValue());
        return pto.map(p -> new PtoBalanceResponse(
                employeeId,
                p.getTotalHours() - p.getUsedHours(),
                p.getUsedHours(),
                p.getTotalHours()
        ));
    }

    public Optional<PtoScheduleResponse> schedulePto(String employeeId, PtoScheduleRequest request) {
        Optional<EmployeePto> pto = ptoRepository.findByEmployeeIdAndYear(employeeId, Year.now().getValue());
        if (pto.isEmpty()) {
            return Optional.empty();
        }

        EmployeePto record = pto.get();
        double remaining = record.getTotalHours() - record.getUsedHours();
        if (request.getHours() > remaining) {
            return Optional.empty();
        }

        record.setUsedHours(record.getUsedHours() + request.getHours());
        ptoRepository.save(record);

        PtoRequest ptoRequest = new PtoRequest();
        ptoRequest.setEmployeeId(employeeId);
        ptoRequest.setStartDate(LocalDate.parse(request.getStartDate()));
        ptoRequest.setEndDate(LocalDate.parse(request.getEndDate()));
        ptoRequest.setHours(request.getHours());
        ptoRequest.setStatus("APPROVED");
        ptoRequestRepository.save(ptoRequest);

        return Optional.of(new PtoScheduleResponse(
                "PTO scheduled successfully",
                UUID.randomUUID().toString(),
                request.getStartDate(),
                request.getEndDate(),
                request.getHours()
        ));
    }
}
