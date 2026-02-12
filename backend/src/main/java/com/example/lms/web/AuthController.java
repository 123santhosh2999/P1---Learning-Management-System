package com.example.lms.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.lms.dto.AuthDtos;
import com.example.lms.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication endpoints (signup, login)")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Signup", description = "Create a new user account and return the created user")
  public AuthDtos.UserResponse signup(@Valid @RequestBody AuthDtos.SignupRequest req) {
    return authService.signup(req);
  }

  @PostMapping("/login")
  @Operation(summary = "Login", description = "Login with email/password and return a JWT token")
  public AuthDtos.LoginResponse login(@Valid @RequestBody AuthDtos.LoginRequest req) {
    return authService.login(req);
  }
}
