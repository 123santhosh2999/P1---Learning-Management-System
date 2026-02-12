package com.example.lms.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.lms.dto.CourseDtos;
import com.example.lms.dto.LessonDtos;
import com.example.lms.dto.ProgressDtos;
import com.example.lms.service.CourseMapper;
import com.example.lms.service.LessonMapper;
import com.example.lms.service.LmsService;
import com.example.lms.util.SecurityUtils;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Student Courses", description = "Student course browsing, enrollment, and progress")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

  private final LmsService lmsService;

  public CourseController(LmsService lmsService) {
    this.lmsService = lmsService;
  }

  @GetMapping
  @Operation(summary = "Browse approved courses", description = "List all approved courses that students can enroll in")
  public List<CourseDtos.CourseResponse> browse() {
    return lmsService.browseApprovedCourses().stream().map(CourseMapper::toDto).toList();
  }

  @PostMapping("/{id}/enroll")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Enroll in a course", description = "Enroll the current student into the given approved course")
  public Object enroll(@PathVariable Long id) {
    Long studentId = SecurityUtils.currentUserId();
    var e = lmsService.enroll(studentId, id);
    return java.util.Map.of(
      "enrollment",
      java.util.Map.of("id", e.getId(), "courseId", e.getCourse().getId(), "studentId", e.getStudent().getId())
    );
  }

  @GetMapping("/{id}/lessons")
  @Operation(summary = "Get course lessons", description = "List lessons for a course (requires enrollment)")
  public List<LessonDtos.LessonResponse> lessons(@PathVariable Long id) {
    Long studentId = SecurityUtils.currentUserId();
    return lmsService.courseLessonsForStudent(studentId, id).stream().map(LessonMapper::toDto).toList();
  }

  @GetMapping("/progress/summary")
  @Operation(summary = "Progress summary", description = "Get progress summary for the current student in a course")
  public ProgressDtos.ProgressSummaryResponse summary(@RequestParam Long courseId) {
    Long studentId = SecurityUtils.currentUserId();
    return lmsService.progressSummary(studentId, courseId);
  }
}
