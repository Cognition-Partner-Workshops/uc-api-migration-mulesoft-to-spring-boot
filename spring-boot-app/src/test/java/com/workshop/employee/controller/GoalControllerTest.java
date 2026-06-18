package com.workshop.employee.controller;

import com.workshop.employee.service.OAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuthService oAuthService;

    private String obtainToken() {
        return oAuthService.authenticate("demo-client", "demo-secret")
                .orElseThrow()
                .getAccessToken();
    }

    @Test
    void returnsGoalsForKnownEmployee() throws Exception {
        String token = obtainToken();

        mockMvc.perform(get("/api/employee/EMP001/goals")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void returns404ForUnknownEmployee() throws Exception {
        String token = obtainToken();

        mockMvc.perform(get("/api/employee/UNKNOWN999/goals")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void returns401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/employee/EMP001/goals"))
                .andExpect(status().isUnauthorized());
    }
}
