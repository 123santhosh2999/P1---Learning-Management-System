package com.example.lms.security;

public record JwtUserClaims(Long id, String email, String role, String name) {}
