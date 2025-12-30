package com.example.lms.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.lms.dto.AuthDtos;
import com.example.lms.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthDtos.UserResponse signup(@Valid @RequestBody AuthDtos.SignupRequest req) {
    return authService.signup(req);
  }

  @PostMapping("/login")
  public AuthDtos.LoginResponse login(@Valid @RequestBody AuthDtos.LoginRequest req) {
    return authService.login(req);
  }
}
