package com.workshop.employee;

import com.workshop.employee.controller.EmployeeController;
import com.workshop.employee.dto.*;
import com.workshop.employee.exception.BadRequestException;
import com.workshop.employee.exception.ResourceNotFoundException;
import com.workshop.employee.service.EmployeeService;
import com.workshop.employee.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerWebMvcTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    EmployeeService employeeService;
    @MockitoBean
    TokenService tokenService;

    @Test
    void allEmployeeEndpointsReturnData() throws Exception {
        when(tokenService.validate("token")).thenReturn(true);
        when(employeeService.goals("1001")).thenReturn(List.of("Goal one"));
        when(employeeService.learningStatus("1001"))
                .thenReturn(new LearningStatusResponse("1001", List.of(new CourseStatus("Course", "IN_PROGRESS", 50))));
        when(employeeService.nextPayDate("1001"))
                .thenReturn(new PayDateResponse("1001", LocalDate.of(2025, 2, 1), "BI_WEEKLY"));
        when(employeeService.ptoBalance("1001"))
                .thenReturn(new PtoBalanceResponse("1001", 144.0, 16.0, 160.0));
        when(employeeService.schedulePto(eq("1001"), any()))
                .thenReturn(new PtoScheduleResponse("PTO scheduled successfully", "PTO-1",
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2), 8.0));

        mockMvc.perform(get("/api/employee/1001/goals").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("Goal one"));
        mockMvc.perform(get("/api/employee/1001/learning-status").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.courses[0].progress").value(50));
        mockMvc.perform(get("/api/employee/1001/next-pay-date").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nextPayDate").value("2025-02-01"));
        mockMvc.perform(get("/api/employee/1001/pto/balance").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(144.0));
        mockMvc.perform(post("/api/employee/1001/pto/schedule").header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate":"2025-01-01","endDate":"2025-01-02","hours":8}
                                """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.hoursScheduled").value(8.0));
    }

    @Test
    void missingEmployeeDataReturns404() throws Exception {
        when(tokenService.validate("token")).thenReturn(true);
        when(employeeService.goals("missing")).thenThrow(new ResourceNotFoundException("missing", "NOT_FOUND"));
        when(employeeService.learningStatus("missing")).thenThrow(new ResourceNotFoundException("missing", "NOT_FOUND"));
        when(employeeService.nextPayDate("missing")).thenThrow(new ResourceNotFoundException("missing", "NOT_FOUND"));
        when(employeeService.ptoBalance("missing")).thenThrow(new ResourceNotFoundException("missing", "NOT_FOUND"));

        mockMvc.perform(get("/api/employee/missing/goals").header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/employee/missing/learning-status").header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/employee/missing/next-pay-date").header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/employee/missing/pto/balance").header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void scheduleValidationReturns400() throws Exception {
        when(tokenService.validate("token")).thenReturn(true);
        when(employeeService.schedulePto(eq("1001"), any()))
                .thenThrow(new BadRequestException("endDate must not be before startDate", "INVALID_DATES"));

        mockMvc.perform(post("/api/employee/1001/pto/schedule").header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate":"2025-01-02","endDate":"2025-01-01","hours":8}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_DATES"));

        when(employeeService.schedulePto(eq("1001"), any()))
                .thenThrow(new BadRequestException("Insufficient PTO balance", "INSUFFICIENT_BALANCE"));
        mockMvc.perform(post("/api/employee/1001/pto/schedule").header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate":"2025-01-01","endDate":"2025-01-02","hours":800}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INSUFFICIENT_BALANCE"));

        mockMvc.perform(post("/api/employee/1001/pto/schedule").header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}
