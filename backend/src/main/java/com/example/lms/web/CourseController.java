package com.example.lms.web;

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
public class CourseController {

  private final LmsService lmsService;

  public CourseController(LmsService lmsService) {
    this.lmsService = lmsService;
  }

  @GetMapping
  public List<CourseDtos.CourseResponse> browse() {
    return lmsService.browseApprovedCourses().stream().map(CourseMapper::toDto).toList();
  }

  @PostMapping("/{id}/enroll")
  @ResponseStatus(HttpStatus.CREATED)
  public Object enroll(@PathVariable Long id) {
    Long studentId = SecurityUtils.currentUserId();
    var e = lmsService.enroll(studentId, id);
    return java.util.Map.of(
      "enrollment",
      java.util.Map.of("id", e.getId(), "courseId", e.getCourse().getId(), "studentId", e.getStudent().getId())
    );
  }

  @GetMapping("/{id}/lessons")
  public List<LessonDtos.LessonResponse> lessons(@PathVariable Long id) {
    Long studentId = SecurityUtils.currentUserId();
    return lmsService.courseLessonsForStudent(studentId, id).stream().map(LessonMapper::toDto).toList();
  }

  @GetMapping("/progress/summary")
  public ProgressDtos.ProgressSummaryResponse summary(@RequestParam Long courseId) {
    Long studentId = SecurityUtils.currentUserId();
    return lmsService.progressSummary(studentId, courseId);
  }
}
