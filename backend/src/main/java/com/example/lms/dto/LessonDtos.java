package com.example.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LessonDtos {

  public record LessonResponse(
    Long id,
    Long courseId,
    String title,
    String contentText,
    String videoUrl,
    String pdfUrl,
    String mediaPath,
    Integer orderIndex
  ) {}

  public record CreateLessonRequest(
    @NotBlank @Size(min = 3, max = 200) String title,
    @Size(max = 20000) String contentText,
    @Size(max = 500) String videoUrl,
    @Size(max = 500) String pdfUrl,
    Integer orderIndex
  ) {}
}
