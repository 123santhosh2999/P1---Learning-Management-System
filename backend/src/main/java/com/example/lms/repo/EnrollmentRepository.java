package com.example.lms.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lms.domain.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
  Optional<Enrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);
  List<Enrollment> findByStudentId(Long studentId);
  List<Enrollment> findByCourseId(Long courseId);
}
