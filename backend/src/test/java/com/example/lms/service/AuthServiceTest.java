package com.example.lms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.lms.domain.Role;
import com.example.lms.domain.User;
import com.example.lms.dto.AuthDtos;
import com.example.lms.repo.UserRepository;
import com.example.lms.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private AuthService authService;

  @Test
  void signup_whenEmailExists_throws() {
    when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

    AuthDtos.SignupRequest req = new AuthDtos.SignupRequest("John", "john@example.com", "secret123");

    assertThatThrownBy(() -> authService.signup(req))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Email already exists");
  }

  @Test
  void login_success_returnsTokenAndUser() {
    User u = new User();
    u.setName("John");
    u.setEmail("john@example.com");
    u.setPasswordHash("hash");
    u.setRole(Role.STUDENT);

    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(u));
    when(passwordEncoder.matches("secret123", "hash")).thenReturn(true);
    when(jwtService.generateToken(org.mockito.ArgumentMatchers.any(com.example.lms.security.JwtUserClaims.class))).thenReturn("token123");

    AuthDtos.LoginResponse resp = authService.login(new AuthDtos.LoginRequest("john@example.com", "secret123"));

    assertThat(resp.token()).isEqualTo("token123");
    assertThat(resp.user().email()).isEqualTo("john@example.com");
  }
}
