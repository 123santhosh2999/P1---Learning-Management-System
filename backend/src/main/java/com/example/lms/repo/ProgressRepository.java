package com.example.lms.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lms.domain.Progress;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
  Optional<Progress> findByStudentIdAndLessonId(Long studentId, Long lessonId);
  List<Progress> findByStudentIdAndLessonCourseId(Long studentId, Long courseId);
}
