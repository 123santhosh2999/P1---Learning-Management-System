package com.example.lms.web;

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
public class AdminController {

  private final LmsService lmsService;

  public AdminController(LmsService lmsService) {
    this.lmsService = lmsService;
  }

  @GetMapping("/users")
  public List<AdminDtos.UserResponse> users() {
    return lmsService.listUsers().stream()
      .map(u -> new AdminDtos.UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole()))
      .toList();
  }

  @PatchMapping("/users/{id}/role")
  public AdminDtos.UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody AdminDtos.UpdateRoleRequest req) {
    var u = lmsService.updateUserRole(id, req.role());
    return new AdminDtos.UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
  }

  @DeleteMapping("/users/{id}")
  public java.util.Map<String, Object> deleteUser(@PathVariable Long id) {
    lmsService.deleteUser(id);
    return java.util.Map.of("ok", true);
  }

  @GetMapping("/courses")
  public List<CourseDtos.CourseResponse> courses(@RequestParam(required = false) CourseStatus status) {
    return lmsService.listCoursesForAdmin(status).stream().map(CourseMapper::toDto).toList();
  }

  @PatchMapping("/courses/{id}/approve")
  public CourseDtos.CourseResponse approve(@PathVariable Long id) {
    return CourseMapper.toDto(lmsService.setCourseStatus(id, CourseStatus.APPROVED));
  }

  @PatchMapping("/courses/{id}/reject")
  public CourseDtos.CourseResponse reject(@PathVariable Long id) {
    return CourseMapper.toDto(lmsService.setCourseStatus(id, CourseStatus.REJECTED));
  }

  @DeleteMapping("/courses/{id}")
  public java.util.Map<String, Object> deleteCourse(@PathVariable Long id) {
    lmsService.deleteCourse(id);
    return java.util.Map.of("ok", true);
  }
}
