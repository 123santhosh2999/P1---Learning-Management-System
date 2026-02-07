package com.example.lms.integration;

import com.example.lms.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthApiIntegrationTest extends IntegrationTestBase {

  @Test
  void signup_then_login_returnsTokenAndRole() throws Exception {
    String email = "it_student_" + System.currentTimeMillis() + "@example.com";

    String signupBody = objectMapper.writeValueAsString(java.util.Map.of(
      "name", "Test Student",
      "email", email,
      "password", "secret123",
      "role", "STUDENT"
    ));

    mockMvc.perform(
        post("/api/auth/signup")
          .contentType(MediaType.APPLICATION_JSON)
          .content(signupBody)
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.email").value(email))
      .andExpect(jsonPath("$.role").value("STUDENT"));

    String token = loginAndGetToken(email, "secret123");
    assertThat(token).isNotBlank();

    // also ensure we can create another role user (instructor)
    String email2 = "it_instructor_" + System.currentTimeMillis() + "@example.com";
    String signupBody2 = objectMapper.writeValueAsString(java.util.Map.of(
      "name", "Test Instructor",
      "email", email2,
      "password", "secret123",
      "role", "INSTRUCTOR"
    ));

    mockMvc.perform(
        post("/api/auth/signup")
          .contentType(MediaType.APPLICATION_JSON)
          .content(signupBody2)
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.role").value(Role.INSTRUCTOR.name()));
  }
}
