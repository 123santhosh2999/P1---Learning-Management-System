package com.example.lms.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.lms.dto.CourseDtos;
import com.example.lms.dto.EnrollmentDtos;
import com.example.lms.dto.LessonDtos;
import com.example.lms.security.JwtUserClaims;
import com.example.lms.service.CourseMapper;
import com.example.lms.service.LessonMapper;
import com.example.lms.service.LmsService;
import com.example.lms.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/instructor")
@Tag(name = "Instructor", description = "Instructor endpoints for managing courses and lessons")
@SecurityRequirement(name = "bearerAuth")
public class InstructorController {

  private final LmsService lmsService;

  public InstructorController(LmsService lmsService) {
    this.lmsService = lmsService;
  }

  private boolean isAdmin() {
    var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    return auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  @PostMapping("/courses")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create course", description = "Create a new course as the current instructor")
  public CourseDtos.CourseResponse createCourse(@Valid @RequestBody CourseDtos.CreateCourseRequest req) {
    Long instructorId = SecurityUtils.currentUserId();
    return CourseMapper.toDto(lmsService.createCourse(instructorId, req));
  }

  @PutMapping("/courses/{id}")
  @Operation(summary = "Update course", description = "Update a course owned by the current instructor (admins can update any course)")
  public CourseDtos.CourseResponse updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDtos.UpdateCourseRequest req) {
    Long instructorId = SecurityUtils.currentUserId();
    return CourseMapper.toDto(lmsService.updateCourse(instructorId, id, req, isAdmin()));
  }

  @GetMapping("/courses")
  @Operation(summary = "My courses", description = "List courses owned by the current instructor (admins see all)")
  public List<CourseDtos.CourseResponse> myCourses() {
    Long instructorId = SecurityUtils.currentUserId();
    return lmsService.myCourses(instructorId, isAdmin()).stream().map(CourseMapper::toDto).toList();
  }

  @GetMapping("/courses/{id}/lessons")
  @Operation(summary = "Course lessons", description = "List lessons for an instructor course")
  public List<LessonDtos.LessonResponse> lessons(@PathVariable Long id) {
    Long instructorId = SecurityUtils.currentUserId();
    return lmsService.courseLessonsForInstructor(instructorId, id, isAdmin()).stream().map(LessonMapper::toDto).toList();
  }

  @PostMapping(value = "/courses/{id}/lessons", consumes = {"multipart/form-data"})
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Add lesson", description = "Add a lesson to a course (multipart: JSON part 'data' + optional file part 'media')")
  public LessonDtos.LessonResponse addLesson(
    @PathVariable Long id,
    @Valid @RequestPart("data") LessonDtos.CreateLessonRequest req,
    @RequestPart(value = "media", required = false) MultipartFile media
  ) {
    Long instructorId = SecurityUtils.currentUserId();
    return LessonMapper.toDto(lmsService.addLesson(instructorId, id, req, media, isAdmin()));
  }

  @GetMapping("/courses/{id}/enrollments")
  @Operation(summary = "Course enrollments", description = "List student enrollments for a course")
  public List<EnrollmentDtos.EnrollmentResponse> enrollments(@PathVariable Long id) {
    Long instructorId = SecurityUtils.currentUserId();
    return lmsService.listCourseEnrollments(instructorId, id, isAdmin()).stream()
      .map(e -> new EnrollmentDtos.EnrollmentResponse(
        e.getId(),
        e.getCourse().getId(),
        e.getCourse().getTitle(),
        e.getCourse().getStatus(),
        e.getCourse().getInstructor() != null ? e.getCourse().getInstructor().getId() : null,
        e.getStudent().getId(),
        e.getStudent().getName(),
        e.getStudent().getEmail()
      ))
      .toList();
  }
}
