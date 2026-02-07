package com.example.lms.integration;

import com.example.lms.domain.Role;
import com.example.lms.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LmsEndToEndFlowIntegrationTest extends IntegrationTestBase {

  private static final String ADMIN_EMAIL = "admin_it@lms.com";
  private static final String INSTRUCTOR_EMAIL = "instructor_it@lms.com";
  private static final String STUDENT_EMAIL = "student_it@lms.com";
  private static final String PASSWORD = "secret123";

  @BeforeEach
  void seedUsers() {
    ensureUser("Admin", ADMIN_EMAIL, PASSWORD, Role.ADMIN);
    ensureUser("Instructor", INSTRUCTOR_EMAIL, PASSWORD, Role.INSTRUCTOR);
    ensureUser("Student", STUDENT_EMAIL, PASSWORD, Role.STUDENT);
  }

  @Test
  void fullFlow_instructorCreates_adminApproves_studentEnrolls_andUpdatesProgress() throws Exception {
    String instructorToken = loginAndGetToken(INSTRUCTOR_EMAIL, PASSWORD);
    String adminToken = loginAndGetToken(ADMIN_EMAIL, PASSWORD);
    String studentToken = loginAndGetToken(STUDENT_EMAIL, PASSWORD);

    // Instructor creates course
    String courseTitle = "IT Course " + System.currentTimeMillis();
    String createCourseBody = objectMapper.writeValueAsString(java.util.Map.of(
      "title", courseTitle,
      "description", "Integration test course"
    ));

    String createdCourseResp = mockMvc.perform(
        post("/api/instructor/courses")
          .header("Authorization", "Bearer " + instructorToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(createCourseBody)
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.title").value(courseTitle))
      .andExpect(jsonPath("$.status").value("PENDING"))
      .andReturn()
      .getResponse()
      .getContentAsString();

    JsonNode createdCourse = objectMapper.readTree(createdCourseResp);
    long courseId = createdCourse.get("id").asLong();

    // Instructor adds lesson (multipart with JSON part)
    var lessonNode = objectMapper.createObjectNode();
    lessonNode.put("title", "Lesson One");
    lessonNode.put("contentText", "Hello");
    lessonNode.putNull("videoUrl");
    lessonNode.putNull("pdfUrl");
    lessonNode.put("orderIndex", 0);
    String lessonJson = objectMapper.writeValueAsString(lessonNode);

    MockMultipartFile dataPart = new MockMultipartFile(
      "data",
      "data.json",
      "application/json",
      lessonJson.getBytes(java.nio.charset.StandardCharsets.UTF_8)
    );

    String lessonResp = mockMvc.perform(
        multipart("/api/instructor/courses/{id}/lessons", courseId)
          .file(dataPart)
          .header("Authorization", "Bearer " + instructorToken)
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.courseId").value((int) courseId))
      .andExpect(jsonPath("$.title").value("Lesson One"))
      .andReturn()
      .getResponse()
      .getContentAsString();

    long lessonId = objectMapper.readTree(lessonResp).get("id").asLong();

    // Admin approves
    mockMvc.perform(
        patch("/api/admin/courses/{id}/approve", courseId)
          .header("Authorization", "Bearer " + adminToken)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("APPROVED"));

    // Student can browse approved courses
    mockMvc.perform(
        get("/api/courses")
          .header("Authorization", "Bearer " + studentToken)
      )
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    // Student enrolls
    mockMvc.perform(
        post("/api/courses/{id}/enroll", courseId)
          .header("Authorization", "Bearer " + studentToken)
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.enrollment.courseId").value((int) courseId));

    // Student can access lessons after enrollment
    mockMvc.perform(
        get("/api/courses/{id}/lessons", courseId)
          .header("Authorization", "Bearer " + studentToken)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].title").value("Lesson One"));

    // Student updates progress
    mockMvc.perform(
        post("/api/lessons/{id}/progress", lessonId)
          .header("Authorization", "Bearer " + studentToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(java.util.Map.of("status", "COMPLETED")))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.progress").exists());

    // Student sees progress summary
    String summaryResp = mockMvc.perform(
        get("/api/courses/progress/summary")
          .header("Authorization", "Bearer " + studentToken)
          .param("courseId", String.valueOf(courseId))
      )
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    JsonNode summary = objectMapper.readTree(summaryResp);
    assertThat(summary.get("totalLessons").asInt()).isGreaterThanOrEqualTo(1);
    assertThat(summary.get("completedLessons").asInt()).isGreaterThanOrEqualTo(1);

    // Instructor can view enrollments
    String enrollmentsResp = mockMvc.perform(
        get("/api/instructor/courses/{id}/enrollments", courseId)
          .header("Authorization", "Bearer " + instructorToken)
      )
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    JsonNode enrollments = objectMapper.readTree(enrollmentsResp);
    assertThat(enrollments.isArray()).isTrue();
    assertThat(enrollments.size()).isGreaterThanOrEqualTo(1);
  }
}
