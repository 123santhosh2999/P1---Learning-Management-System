package com.example.lms.dto;

import com.example.lms.domain.CourseStatus;

public class EnrollmentDtos {

  public record EnrollmentResponse(
    Long id,
    Long courseId,
    String courseTitle,
    CourseStatus courseStatus,
    Long instructorId,
    Long studentId,
    String studentName,
    String studentEmail
  ) {}
}
