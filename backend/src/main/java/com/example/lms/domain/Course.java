package com.example.lms.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Lob
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CourseStatus status = CourseStatus.PENDING;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "instructor_id", nullable = false)
  private User instructor;

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CourseStatus getStatus() {
    return status;
  }

  public void setStatus(CourseStatus status) {
    this.status = status;
  }

  public User getInstructor() {
    return instructor;
  }

  public void setInstructor(User instructor) {
    this.instructor = instructor;
  }
}
