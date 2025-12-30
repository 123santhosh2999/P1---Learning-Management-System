package com.example.lms.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> badRequest(IllegalArgumentException e) {
    return Map.of("message", e.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Map<String, Object> unauthorized(IllegalStateException e) {
    return Map.of("message", e.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String, Object> forbidden(AccessDeniedException e) {
    return Map.of("message", "Forbidden");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> validation(MethodArgumentNotValidException e) {
    var first = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
    String msg = first != null ? first.getField() + ": " + first.getDefaultMessage() : "Validation error";
    return Map.of("message", msg);
  }
}
