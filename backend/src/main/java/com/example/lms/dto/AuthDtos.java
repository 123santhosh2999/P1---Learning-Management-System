package com.example.lms.dto;

import com.example.lms.domain.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

  public record SignupRequest(
    @NotBlank @Size(min = 2, max = 120) String name,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6, max = 100) String password
  ) {}

  public record LoginRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6, max = 100) String password
  ) {}

  public record UserResponse(Long id, String name, String email, Role role) {}

  public record LoginResponse(String token, UserResponse user) {}
}
