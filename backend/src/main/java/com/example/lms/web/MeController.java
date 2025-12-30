package com.example.lms.web;

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
public class MeController {

  private final LmsService lmsService;

  public MeController(LmsService lmsService) {
    this.lmsService = lmsService;
  }

  @GetMapping
  public AuthDtos.UserResponse me() {
    var claims = SecurityUtils.currentClaims();
    return new AuthDtos.UserResponse(claims.id(), claims.name(), claims.email(), com.example.lms.domain.Role.valueOf(claims.role()));
  }

  @GetMapping("/enrollments")
  public List<EnrollmentDtos.EnrollmentResponse> myEnrollments() {
    Long studentId = SecurityUtils.currentUserId();
    return lmsService.myEnrollments(studentId).stream()
      .map(e -> new EnrollmentDtos.EnrollmentResponse(e.getId(), e.getCourse().getId(), e.getStudent().getId()))
      .toList();
  }
}
