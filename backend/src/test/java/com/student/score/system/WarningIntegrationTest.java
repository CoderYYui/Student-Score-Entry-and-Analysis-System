package com.student.score.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WarningIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGenerateWarningsForRiskStudents() throws Exception {
        mockMvc.perform(get("/api/warnings/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentName").exists())
                .andExpect(jsonPath("$[0].riskItems").isArray());
                // .andExpect(jsonPath("$[0].emotionLabel").isNotEmpty());
    }

    @Test
    void shouldReturnStudentWarningSummary() throws Exception {
        mockMvc.perform(get("/api/warnings/student/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value("Software Engineering"));
                // .andExpect(jsonPath("$[0].communicationAdvice").isNotEmpty());
    }
}