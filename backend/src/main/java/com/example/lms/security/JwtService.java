package com.example.lms.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final SecretKey key;
  private final long expirationMs;

  public JwtService(
    @Value("${app.jwt.secret}") String secret,
    @Value("${app.jwt.expiration-ms}") long expirationMs
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public String generateToken(Long userId, String email, String role, String name) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
      .subject(String.valueOf(userId))
      .issuedAt(now)
      .expiration(exp)
      .claim("email", email)
      .claim("role", role)
      .claim("name", name)
      .signWith(key)
      .compact();
  }

  public JwtUserClaims parse(String token) {
    var claims = Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload();

    Long userId = Long.parseLong(claims.getSubject());
    String email = claims.get("email", String.class);
    String role = claims.get("role", String.class);
    String name = claims.get("name", String.class);

    return new JwtUserClaims(userId, email, role, name);
  }
}
