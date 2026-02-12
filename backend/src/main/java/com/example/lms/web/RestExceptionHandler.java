package com.example.lms.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class RestExceptionHandler {

  private static ResponseEntity<ApiError> toResponse(HttpStatus status, String message, HttpServletRequest request, Map<String, String> fieldErrors) {
    ApiError body = new ApiError(
      status.value(),
      status.getReasonPhrase(),
      message,
      request.getRequestURI(),
      null,
      fieldErrors
    );
    return ResponseEntity.status(status).body(body);
  }

  private static ResponseEntity<ApiError> toResponse(HttpStatus status, String message, HttpServletRequest request) {
    return toResponse(status, message, request, null);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> illegalArgument(IllegalArgumentException e, HttpServletRequest request) {
    String msg = e.getMessage() != null ? e.getMessage() : "Bad request";
    String lowered = msg.toLowerCase();

    if (lowered.endsWith("not found")) {
      return toResponse(HttpStatus.NOT_FOUND, msg, request);
    }
    if (lowered.equals("forbidden")) {
      return toResponse(HttpStatus.FORBIDDEN, msg, request);
    }
    if (lowered.contains("already") || lowered.contains("exists")) {
      return toResponse(HttpStatus.CONFLICT, msg, request);
    }
    return toResponse(HttpStatus.BAD_REQUEST, msg, request);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiError> illegalState(IllegalStateException e, HttpServletRequest request) {
    String msg = e.getMessage() != null ? e.getMessage() : "Unauthorized";
    return toResponse(HttpStatus.UNAUTHORIZED, msg, request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> forbidden(AccessDeniedException e, HttpServletRequest request) {
    return toResponse(HttpStatus.FORBIDDEN, "Forbidden", request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
    Map<String, String> fieldErrors = new LinkedHashMap<>();
    e.getBindingResult().getFieldErrors().forEach(fe -> {
      if (!fieldErrors.containsKey(fe.getField())) {
        fieldErrors.put(fe.getField(), fe.getDefaultMessage());
      }
    });

    String msg = fieldErrors.isEmpty() ? "Validation error" : "Validation error";
    return toResponse(HttpStatus.BAD_REQUEST, msg, request, fieldErrors);
  }

  @ExceptionHandler({
    MethodArgumentTypeMismatchException.class,
    MissingServletRequestParameterException.class,
    HttpMessageNotReadableException.class
  })
  public ResponseEntity<ApiError> requestParsing(Exception e, HttpServletRequest request) {
    String msg = "Bad request";
    if (e instanceof MissingServletRequestParameterException ex) {
      msg = "Missing parameter: " + ex.getParameterName();
    } else if (e instanceof MethodArgumentTypeMismatchException ex) {
      msg = "Invalid value for: " + ex.getName();
    } else if (e instanceof HttpMessageNotReadableException) {
      msg = "Malformed JSON request";
    }
    return toResponse(HttpStatus.BAD_REQUEST, msg, request);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiError> responseStatus(ResponseStatusException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    String msg = e.getReason() != null ? e.getReason() : status.getReasonPhrase();
    return toResponse(status, msg, request);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> conflict(DataIntegrityViolationException e, HttpServletRequest request) {
    return toResponse(HttpStatus.CONFLICT, "Conflict", request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> fallback(Exception e, HttpServletRequest request) {
    return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
  }
}
