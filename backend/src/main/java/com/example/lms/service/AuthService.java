package com.example.lms.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class AuthService {

    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkeymysecretkey"; // ≥32 chars

    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 1 day

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(Long userId, String email, String role, String name) {
        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of(
                        "userId", userId,
                        "role", role,
                        "name", name
                ))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
