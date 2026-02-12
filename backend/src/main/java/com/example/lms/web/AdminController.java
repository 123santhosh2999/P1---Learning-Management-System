package com.example.lms.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.lms.domain.CourseStatus;
import com.example.lms.dto.AdminDtos;
import com.example.lms.dto.CourseDtos;
import com.example.lms.service.CourseMapper;
import com.example.lms.service.LmsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin-only endpoints for users and course moderation")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

  private final LmsService lmsService;

  public AdminController(LmsService lmsService) {
    this.lmsService = lmsService;
  }

  @GetMapping("/users")
  @Operation(summary = "List users", description = "List all users")
  public List<AdminDtos.UserResponse> users() {
    return lmsService.listUsers().stream()
      .map(u -> new AdminDtos.UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole()))
      .toList();
  }

  @PatchMapping("/users/{id}/role")
  @Operation(summary = "Update user role", description = "Change a user's role")
  public AdminDtos.UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody AdminDtos.UpdateRoleRequest req) {
    var u = lmsService.updateUserRole(id, req.role());
    return new AdminDtos.UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
  }

  @DeleteMapping("/users/{id}")
  @Operation(summary = "Delete user", description = "Delete a user by id")
  public java.util.Map<String, Object> deleteUser(@PathVariable Long id) {
    lmsService.deleteUser(id);
    return java.util.Map.of("ok", true);
  }

  @GetMapping("/courses")
  @Operation(summary = "List courses", description = "List all courses, optionally filtered by status")
  public List<CourseDtos.CourseResponse> courses(@RequestParam(required = false) CourseStatus status) {
    return lmsService.listCoursesForAdmin(status).stream().map(CourseMapper::toDto).toList();
  }

  @PatchMapping("/courses/{id}/approve")
  @Operation(summary = "Approve course", description = "Approve a pending course")
  public CourseDtos.CourseResponse approve(@PathVariable Long id) {
    return CourseMapper.toDto(lmsService.setCourseStatus(id, CourseStatus.APPROVED));
  }

  @PatchMapping("/courses/{id}/reject")
  @Operation(summary = "Reject course", description = "Reject a pending course")
  public CourseDtos.CourseResponse reject(@PathVariable Long id) {
    return CourseMapper.toDto(lmsService.setCourseStatus(id, CourseStatus.REJECTED));
  }

  @DeleteMapping("/courses/{id}")
  @Operation(summary = "Delete course", description = "Delete a course by id")
  public java.util.Map<String, Object> deleteCourse(@PathVariable Long id) {
    lmsService.deleteCourse(id);
    return java.util.Map.of("ok", true);
  }
}
