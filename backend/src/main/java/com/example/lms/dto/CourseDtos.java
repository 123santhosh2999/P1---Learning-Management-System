package com.example.lms.dto;

import com.example.lms.domain.CourseStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CourseDtos {

  public record CourseResponse(
    Long id,
    String title,
    String description,
    CourseStatus status,
    Long instructorId
  ) {}

  public record CreateCourseRequest(
    @NotBlank @Size(min = 3, max = 200) String title,
    @Size(max = 5000) String description
  ) {}

  public record UpdateCourseRequest(
    @Size(min = 3, max = 200) String title,
    @Size(max = 5000) String description
  ) {}
}
