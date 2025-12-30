package com.example.lms.web;

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
  public CourseDtos.CourseResponse createCourse(@Valid @RequestBody CourseDtos.CreateCourseRequest req) {
    Long instructorId = SecurityUtils.currentUserId();
    return CourseMapper.toDto(lmsService.createCourse(instructorId, req));
  }

  @PutMapping("/courses/{id}")
  public CourseDtos.CourseResponse updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDtos.UpdateCourseRequest req) {
    Long instructorId = SecurityUtils.currentUserId();
    return CourseMapper.toDto(lmsService.updateCourse(instructorId, id, req, isAdmin()));
  }

  @PostMapping(value = "/courses/{id}/lessons", consumes = {"multipart/form-data"})
  @ResponseStatus(HttpStatus.CREATED)
  public LessonDtos.LessonResponse addLesson(
    @PathVariable Long id,
    @Valid @RequestPart("data") LessonDtos.CreateLessonRequest req,
    @RequestPart(value = "media", required = false) MultipartFile media
  ) {
    Long instructorId = SecurityUtils.currentUserId();
    return LessonMapper.toDto(lmsService.addLesson(instructorId, id, req, media, isAdmin()));
  }

  @GetMapping("/courses/{id}/enrollments")
  public List<EnrollmentDtos.EnrollmentResponse> enrollments(@PathVariable Long id) {
    Long instructorId = SecurityUtils.currentUserId();
    return lmsService.listCourseEnrollments(instructorId, id, isAdmin()).stream()
      .map(e -> new EnrollmentDtos.EnrollmentResponse(e.getId(), e.getCourse().getId(), e.getStudent().getId()))
      .toList();
  }
}
