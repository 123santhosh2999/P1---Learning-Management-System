package com.example.lms.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.lms.domain.Role;
import com.example.lms.dto.AuthDtos;
import com.example.lms.service.AuthService;

class AuthControllerTest {

  @Test
  void signup_returnsCreatedAndUserResponse() throws Exception {
    AuthService authService = org.mockito.Mockito.mock(AuthService.class);
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();

    AuthDtos.UserResponse resp = new AuthDtos.UserResponse(1L, "John", "john@example.com", Role.STUDENT);
    when(authService.signup(org.mockito.ArgumentMatchers.any(AuthDtos.SignupRequest.class))).thenReturn(resp);

    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"John\",\"email\":\"john@example.com\",\"password\":\"secret123\"}"))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.email").value("john@example.com"));
  }

  @Test
  void login_returnsTokenAndUser() throws Exception {
    AuthService authService = org.mockito.Mockito.mock(AuthService.class);
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();

    AuthDtos.UserResponse user = new AuthDtos.UserResponse(1L, "John", "john@example.com", Role.STUDENT);
    AuthDtos.LoginResponse resp = new AuthDtos.LoginResponse("token123", user);
    when(authService.login(org.mockito.ArgumentMatchers.any(AuthDtos.LoginRequest.class))).thenReturn(resp);

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"john@example.com\",\"password\":\"secret123\"}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.token").value("token123"))
      .andExpect(jsonPath("$.user.email").value("john@example.com"));
  }
}
