package com.example.lms.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.lms.security.JwtUserClaims;

public final class SecurityUtils {

  private SecurityUtils() {}

  public static JwtUserClaims currentClaims() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof JwtUserClaims claims)) {
      throw new IllegalStateException("Unauthorized");
    }
    return claims;
  }

  public static Long currentUserId() {
    return currentClaims().id();
  }

  public static String currentRole() {
    return currentClaims().role();
  }
}
