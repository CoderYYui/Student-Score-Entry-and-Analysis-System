package com.student.score.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.score.system.entity.User;
import com.student.score.system.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldRegisterApproveAndLoginUser() throws Exception {
        String username = "newstudent01";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"username\": \"newstudent01\",
                                  \"password\": \"123456\",
                                  \"name\": \"New Student\",
                                  \"email\": \"newstudent01@example.com\",
                                  \"role\": \"STUDENT\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册申请已提交，请等待教学秘书审批"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"username\": \"newstudent01\",
                                  \"password\": \"123456\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("账号尚未审批，请联系教学秘书"));

        User pendingUser = userMapper.findByUsername(username);
        assertThat(pendingUser).isNotNull();
        assertThat(pendingUser.getStatus()).isEqualTo("PENDING");

        mockMvc.perform(post("/api/secretary/approve-user/{id}", pendingUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"status\": \"APPROVED\",
                                  \"role\": \"STUDENT\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"username\": \"newstudent01\",
                                  \"password\": \"123456\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        assertThat(jsonNode.path("user").path("role").asText()).isEqualTo("STUDENT");
        assertThat(jsonNode.path("token").asText()).contains("fake-jwt-token");
    }
}