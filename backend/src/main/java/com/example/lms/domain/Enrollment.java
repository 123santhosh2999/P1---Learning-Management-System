package com.example.lms.domain;

import jakarta.persistence.*;

@Entity
@Table(
  name = "enrollments",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_enrollment_course_student", columnNames = {"course_id", "student_id"})
  }
)
public class Enrollment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  public Long getId() {
    return id;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public User getStudent() {
    return student;
  }

  public void setStudent(User student) {
    this.student = student;
  }
}
