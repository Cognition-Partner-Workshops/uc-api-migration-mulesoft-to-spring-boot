package com.workshop.employee.controller;

import com.workshop.employee.exception.GlobalExceptionHandler;
import com.workshop.employee.exception.NotFoundException;
import com.workshop.employee.service.EmployeeService;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean EmployeeService service;

    @Test
    void unknownGoalsReturnErrorShape() throws Exception {
        when(service.getGoals("UNKNOWN")).thenThrow(new NotFoundException("No goals found for employee UNKNOWN"));
        mvc.perform(get("/api/employee/UNKNOWN/goals"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.message").value("No goals found for employee UNKNOWN"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void goalsAreStringArray() throws Exception {
        when(service.getGoals("EMP001")).thenReturn(List.of("Goal"));
        mvc.perform(get("/api/employee/EMP001/goals"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("Goal"));
    }
}
