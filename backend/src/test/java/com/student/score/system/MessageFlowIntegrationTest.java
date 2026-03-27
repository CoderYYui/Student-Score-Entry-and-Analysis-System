package com.student.score.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MessageFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldAllowStudentQuestionAndTeacherReply() throws Exception {
        String createResponse = mockMvc.perform(post("/api/messages/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"courseId\": 1,
                                  \"studentId\": 5,
                                  \"teacherId\": 3,
                                  \"questionText\": \"老师您好，我对这次成绩有疑问，最近压力有点大。\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createdJson = objectMapper.readTree(createResponse);
        long messageId = createdJson.path("data").path("id").asLong();
        assertThat(messageId).isPositive();

        mockMvc.perform(get("/api/messages/course/1/teacher/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentName").exists())
                .andExpect(jsonPath("$[0].questionText").isNotEmpty());

        mockMvc.perform(post("/api/messages/{id}/reply", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"replyText\": \"已收到你的疑问，我会结合评分标准与你逐项核对。\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/messages/course/1/student/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].replyText").value("已收到你的疑问，我会结合评分标准与你逐项核对。"));
    }
}