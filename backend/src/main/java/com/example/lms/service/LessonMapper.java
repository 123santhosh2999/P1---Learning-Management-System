package com.example.lms.service;

import com.example.lms.domain.Lesson;
import com.example.lms.dto.LessonDtos;

public final class LessonMapper {

  private LessonMapper() {}

  public static LessonDtos.LessonResponse toDto(Lesson lesson) {
    return new LessonDtos.LessonResponse(
      lesson.getId(),
      lesson.getCourse() != null ? lesson.getCourse().getId() : null,
      lesson.getTitle(),
      lesson.getContentText(),
      lesson.getVideoUrl(),
      lesson.getPdfUrl(),
      lesson.getMediaPath(),
      lesson.getOrderIndex()
    );
  }
}
