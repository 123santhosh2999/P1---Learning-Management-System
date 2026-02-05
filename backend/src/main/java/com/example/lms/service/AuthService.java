package com.example.lms.service;

import com.example.lms.domain.Role;
import com.example.lms.domain.User;
import com.example.lms.dto.AuthDtos;
import com.example.lms.repo.UserRepository;
import com.example.lms.security.JwtService;
import com.example.lms.security.JwtUserClaims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public AuthDtos.UserResponse signup(AuthDtos.SignupRequest req) {
    if (userRepository.existsByEmail(req.email())) {
      throw new IllegalArgumentException("Email already exists");
    }

    User user = new User();
    user.setName(req.name());
    user.setEmail(req.email());
    user.setPasswordHash(passwordEncoder.encode(req.password()));

    if (userRepository.count() == 0) {
      user.setRole(Role.ADMIN);
    }

    User saved = userRepository.save(user);
    return new AuthDtos.UserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
  }

  public AuthDtos.LoginResponse login(AuthDtos.LoginRequest req) {
    User user = userRepository.findByEmail(req.email())
      .orElseThrow(() -> new IllegalStateException("Invalid credentials"));

    if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
      throw new IllegalStateException("Invalid credentials");
    }

    String token = jwtService.generateToken(new JwtUserClaims(
      user.getId(),
      user.getEmail(),
      user.getRole().name(),
      user.getName()
    ));
    AuthDtos.UserResponse userResp = new AuthDtos.UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    return new AuthDtos.LoginResponse(token, userResp);
  }
}
