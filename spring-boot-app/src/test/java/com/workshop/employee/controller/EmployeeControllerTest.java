package com.workshop.employee.controller;

import com.workshop.employee.dto.CourseStatus;
import com.workshop.employee.dto.LearningStatusResponse;
import com.workshop.employee.dto.PtoBalanceResponse;
import com.workshop.employee.dto.PtoScheduleRequest;
import com.workshop.employee.dto.PtoScheduleResponse;
import com.workshop.employee.exception.InvalidRequestException;
import com.workshop.employee.exception.NotFoundException;
import com.workshop.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void getGoalsReturnsArray() throws Exception {
        when(employeeService.getGoals("EMP001")).thenReturn(List.of("Goal A", "Goal B"));

        mockMvc.perform(get("/api/employee/EMP001/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Goal A"))
                .andExpect(jsonPath("$[1]").value("Goal B"));
    }

    @Test
    void getGoalsUnknownEmployeeReturns404WithErrorShape() throws Exception {
        when(employeeService.getGoals("UNKNOWN999"))
                .thenThrow(new NotFoundException("No goals found for employee UNKNOWN999"));

        mockMvc.perform(get("/api/employee/UNKNOWN999/goals"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No goals found for employee UNKNOWN999"))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getLearningStatusReturnsCourses() throws Exception {
        when(employeeService.getLearningStatus("EMP001")).thenReturn(new LearningStatusResponse(
                "EMP001", List.of(new CourseStatus("Spring Boot Fundamentals", "COMPLETED", 100))));

        mockMvc.perform(get("/api/employee/EMP001/learning-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.courses[0].courseName").value("Spring Boot Fundamentals"))
                .andExpect(jsonPath("$.courses[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.courses[0].progress").value(100));
    }

    @Test
    void getPtoBalanceReturnsBalance() throws Exception {
        when(employeeService.getPtoBalance("EMP001"))
                .thenReturn(new PtoBalanceResponse("EMP001", 120.0, 40.0, 160.0));

        mockMvc.perform(get("/api/employee/EMP001/pto/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.balance").value(120.0))
                .andExpect(jsonPath("$.used").value(40.0))
                .andExpect(jsonPath("$.total").value(160.0));
    }

    @Test
    void schedulePtoReturnsResponse() throws Exception {
        when(employeeService.schedulePto(eq("EMP001"), any(PtoScheduleRequest.class)))
                .thenReturn(new PtoScheduleResponse(
                        "PTO scheduled successfully", "1",
                        LocalDate.of(2025, 12, 22), LocalDate.of(2025, 12, 26), 40.0));

        mockMvc.perform(post("/api/employee/EMP001/pto/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate":"2025-12-22","endDate":"2025-12-26","hours":40.0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PTO scheduled successfully"))
                .andExpect(jsonPath("$.hoursScheduled").value(40.0));
    }

    @Test
    void schedulePtoInvalidRequestReturns400() throws Exception {
        when(employeeService.schedulePto(eq("EMP001"), any(PtoScheduleRequest.class)))
                .thenThrow(new InvalidRequestException("Insufficient PTO balance"));

        mockMvc.perform(post("/api/employee/EMP001/pto/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate":"2025-12-22","endDate":"2025-12-26","hours":400.0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient PTO balance"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"));
    }
}
