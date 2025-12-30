package com.example.lms.domain;

import jakarta.persistence.*;

@Entity
@Table(
  name = "progress",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_progress_student_lesson", columnNames = {"student_id", "lesson_id"})
  }
)
public class Progress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "lesson_id", nullable = false)
  private Lesson lesson;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ProgressStatus status = ProgressStatus.NOT_STARTED;

  public Long getId() {
    return id;
  }

  public User getStudent() {
    return student;
  }

  public void setStudent(User student) {
    this.student = student;
  }

  public Lesson getLesson() {
    return lesson;
  }

  public void setLesson(Lesson lesson) {
    this.lesson = lesson;
  }

  public ProgressStatus getStatus() {
    return status;
  }

  public void setStatus(ProgressStatus status) {
    this.status = status;
  }
}
