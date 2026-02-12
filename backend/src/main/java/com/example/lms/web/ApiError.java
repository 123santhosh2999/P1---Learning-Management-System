package com.example.lms.web;

import java.time.Instant;
import java.util.Map;

public record ApiError(
  int status,
  String error,
  String message,
  String path,
  Instant timestamp,
  Map<String, String> fieldErrors
) {
  public ApiError {
    if (timestamp == null) {
      timestamp = Instant.now();
    }
  }
}
