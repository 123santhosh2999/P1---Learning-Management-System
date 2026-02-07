package com.example.lms.integration;

import com.example.lms.domain.Role;
import com.example.lms.domain.User;
import com.example.lms.repo.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class IntegrationTestBase {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected UserRepository userRepo;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  protected User ensureUser(String name, String email, String rawPassword, Role role) {
    return userRepo.findByEmail(email).orElseGet(() -> {
      User u = new User();
      u.setName(name);
      u.setEmail(email);
      u.setPasswordHash(passwordEncoder.encode(rawPassword));
      u.setRole(role);
      return userRepo.save(u);
    });
  }

  protected String loginAndGetToken(String email, String password) throws Exception {
    String body = objectMapper.writeValueAsString(java.util.Map.of(
      "email", email,
      "password", password
    ));

    String resp = mockMvc.perform(
        post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(body)
      )
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    JsonNode json = objectMapper.readTree(resp);
    return json.get("token").asText();
  }
}
