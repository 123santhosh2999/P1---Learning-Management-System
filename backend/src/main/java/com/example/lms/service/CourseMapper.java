package com.example.lms.service;

import com.example.lms.domain.Course;
import com.example.lms.dto.CourseDtos;

public final class CourseMapper {

  private CourseMapper() {}

  public static CourseDtos.CourseResponse toDto(Course course) {
    return new CourseDtos.CourseResponse(
      course.getId(),
      course.getTitle(),
      course.getDescription(),
      course.getStatus(),
      course.getInstructor() != null ? course.getInstructor().getId() : null
    );
  }
}
