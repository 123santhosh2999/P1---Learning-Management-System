package com.example.lms.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lms.dto.AuthDtos;
import com.example.lms.dto.EnrollmentDtos;
import com.example.lms.service.LmsService;
import com.example.lms.util.SecurityUtils;

@RestController
@RequestMapping("/api/me")
@Tag(name = "Me", description = "Current user profile and enrollments")
@SecurityRequirement(name = "bearerAuth")
public class MeController {

  private final LmsService lmsService;

  public MeController(LmsService lmsService) {
    this.lmsService = lmsService;
  }

  @GetMapping
  @Operation(summary = "Current user", description = "Return the currently authenticated user")
  public AuthDtos.UserResponse me() {
    var claims = SecurityUtils.currentClaims();
    return new AuthDtos.UserResponse(claims.id(), claims.name(), claims.email(), com.example.lms.domain.Role.valueOf(claims.role()));
  }

  @GetMapping("/enrollments")
  @Operation(summary = "My enrollments", description = "List courses the current student is enrolled in")
  public List<EnrollmentDtos.EnrollmentResponse> myEnrollments() {
    Long studentId = SecurityUtils.currentUserId();
    return lmsService.myEnrollments(studentId).stream()
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
