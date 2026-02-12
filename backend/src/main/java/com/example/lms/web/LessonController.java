package com.example.lms.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import com.example.lms.dto.ProgressDtos;
import com.example.lms.service.LmsService;
import com.example.lms.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lessons")
@Tag(name = "Lessons", description = "Lesson progress endpoints")
@SecurityRequirement(name = "bearerAuth")
public class LessonController {

  private final LmsService lmsService;

  public LessonController(LmsService lmsService) {
    this.lmsService = lmsService;
  }

  @PostMapping("/{id}/progress")
  @Operation(summary = "Update lesson progress", description = "Update progress for the current student in a lesson")
  public Object updateProgress(@PathVariable Long id, @Valid @RequestBody ProgressDtos.UpdateProgressRequest req) {
    Long studentId = SecurityUtils.currentUserId();
    var p = lmsService.updateLessonProgress(studentId, id, req.status());
    return java.util.Map.of(
      "progress",
      java.util.Map.of("id", p.getId(), "studentId", p.getStudent().getId(), "lessonId", p.getLesson().getId(), "status", p.getStatus())
    );
  }
}
