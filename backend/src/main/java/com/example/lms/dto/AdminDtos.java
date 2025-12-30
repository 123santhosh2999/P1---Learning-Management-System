package com.example.lms.dto;

import com.example.lms.domain.Role;

import jakarta.validation.constraints.NotNull;

public class AdminDtos {

  public record UserResponse(Long id, String name, String email, Role role) {}

  public record UpdateRoleRequest(@NotNull Role role) {}
}
