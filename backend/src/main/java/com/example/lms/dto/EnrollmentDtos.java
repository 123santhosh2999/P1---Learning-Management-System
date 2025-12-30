package com.example.lms.dto;

public class EnrollmentDtos {

  public record EnrollmentResponse(Long id, Long courseId, Long studentId) {}
}
