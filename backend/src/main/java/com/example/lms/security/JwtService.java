package com.example.lms.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key =
            Keys.hmacShaKeyFor("my-super-secret-key-my-super-secret-key".getBytes());

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    public String generateToken(JwtUserClaims claims) {
        return Jwts.builder()
                .subject(claims.email())
                .claim("id", claims.id())
                .claim("email", claims.email())
                .claim("role", claims.role())
                .claim("name", claims.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public JwtUserClaims parse(String token) {
        var payload = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long id = null;
        Object rawId = payload.get("id");
        if (rawId instanceof Number n) {
            id = n.longValue();
        } else if (rawId != null) {
            try {
                id = Long.parseLong(rawId.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        String email = payload.get("email", String.class);
        if (email == null) {
            email = payload.getSubject();
        }

        String role = payload.get("role", String.class);
        String name = payload.get("name", String.class);
        return new JwtUserClaims(id, email, role, name);
    }
}
