package com.example.lms.dto;

import com.example.lms.domain.ProgressStatus;

import jakarta.validation.constraints.NotNull;

public class ProgressDtos {

  public record UpdateProgressRequest(@NotNull ProgressStatus status) {}

  public record ProgressSummaryResponse(int totalLessons, int completedLessons) {}
}
