package com.example.lms.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.domain.Role;
import com.example.lms.domain.User;
import com.example.lms.dto.AuthDtos;
import com.example.lms.repo.UserRepository;
import com.example.lms.security.JwtService;

@Service
public class AuthService {

  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthDtos.UserResponse signup(AuthDtos.SignupRequest req) {
    if (userRepo.existsByEmail(req.email())) {
      throw new IllegalArgumentException("Email already registered");
    }

    User user = new User();
    user.setName(req.name());
    user.setEmail(req.email());
    user.setPasswordHash(passwordEncoder.encode(req.password()));

    long count = userRepo.count();
    user.setRole(count == 0 ? Role.ADMIN : Role.STUDENT);

    User saved = userRepo.save(user);
    return new AuthDtos.UserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
  }

  @Transactional(readOnly = true)
  public AuthDtos.LoginResponse login(AuthDtos.LoginRequest req) {
    User user = userRepo.findByEmail(req.email())
      .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

    if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
      throw new IllegalArgumentException("Invalid credentials");
    }

    String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name(), user.getName());
    var userResp = new AuthDtos.UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    return new AuthDtos.LoginResponse(token, userResp);
  }
}
