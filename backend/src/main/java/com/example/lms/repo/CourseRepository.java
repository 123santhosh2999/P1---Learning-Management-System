package com.example.lms.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lms.domain.Course;
import com.example.lms.domain.CourseStatus;

public interface CourseRepository extends JpaRepository<Course, Long> {
  List<Course> findByStatusOrderByIdDesc(CourseStatus status);
}
