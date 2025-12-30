package com.example.lms.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lms.domain.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
  List<Lesson> findByCourseIdOrderByOrderIndexAscIdAsc(Long courseId);
}
