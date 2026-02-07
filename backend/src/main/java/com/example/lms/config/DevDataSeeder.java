package com.example.lms.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.domain.Course;
import com.example.lms.domain.CourseStatus;
import com.example.lms.domain.Lesson;
import com.example.lms.domain.Role;
import com.example.lms.domain.User;
import com.example.lms.repo.CourseRepository;
import com.example.lms.repo.EnrollmentRepository;
import com.example.lms.repo.LessonRepository;
import com.example.lms.repo.UserRepository;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DevDataSeeder implements CommandLineRunner {

  private final UserRepository userRepo;
  private final CourseRepository courseRepo;
  private final LessonRepository lessonRepo;
  private final EnrollmentRepository enrollmentRepo;
  private final PasswordEncoder passwordEncoder;

  public DevDataSeeder(
    UserRepository userRepo,
    CourseRepository courseRepo,
    LessonRepository lessonRepo,
    EnrollmentRepository enrollmentRepo,
    PasswordEncoder passwordEncoder
  ) {
    this.userRepo = userRepo;
    this.courseRepo = courseRepo;
    this.lessonRepo = lessonRepo;
    this.enrollmentRepo = enrollmentRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) {
    User admin = ensureUser("Admin", "admin@lms.com", "admin123", Role.ADMIN);
    User instructor = ensureUser("Instructor", "instructor@lms.com", "instructor123", Role.INSTRUCTOR);
    User instructor2 = ensureUser("Instructor Two", "instructor2@lms.com", "instructor123", Role.INSTRUCTOR);
    User student = ensureUser("Student", "student@lms.com", "student123", Role.STUDENT);
    User student2 = ensureUser("Student Two", "student2@lms.com", "student123", Role.STUDENT);

    Course javaBasics = ensureCourse("Java Basics", "Intro course seeded on startup", instructor, CourseStatus.APPROVED);
    ensureLesson(javaBasics, "Welcome", "Welcome to the course", 1);
    ensureLesson(javaBasics, "Variables", "Learn variables and types", 2);
    ensureEnrollment(javaBasics, student);

    Course reactFundamentals = ensureCourse("React Fundamentals", "Build modern UIs with React", instructor2, CourseStatus.APPROVED);
    ensureLesson(reactFundamentals, "JSX & Components", "Learn how components work", 1);
    ensureLesson(reactFundamentals, "State & Effects", "Manage state and side effects", 2);
    ensureEnrollment(reactFundamentals, student);
    ensureEnrollment(reactFundamentals, student2);

    Course springMasterclass = ensureCourse("Spring Boot Masterclass", "REST APIs, Security & JPA", instructor, CourseStatus.PENDING);
    ensureLesson(springMasterclass, "Project setup", "How to start a Spring Boot project", 1);
    ensureLesson(springMasterclass, "JWT Security", "Secure endpoints with JWT", 2);

    Course dbDesign = ensureCourse("Database Design", "Normalization, indexes, and modeling", instructor2, CourseStatus.REJECTED);
    ensureLesson(dbDesign, "Normalization", "Design normalized schemas", 1);
    ensureLesson(dbDesign, "Indexes", "Speed up queries with indexes", 2);

    userRepo.save(admin);
  }

  private User ensureUser(String name, String email, String rawPassword, Role role) {
    return userRepo.findByEmail(email)
      .orElseGet(() -> {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        return userRepo.save(u);
      });
  }

  private Course ensureCourse(String title, String description, User instructor, CourseStatus status) {
    return courseRepo.findByTitle(title)
      .orElseGet(() -> {
        Course c = new Course();
        c.setTitle(title);
        c.setDescription(description);
        c.setInstructor(instructor);
        c.setStatus(status);
        return courseRepo.save(c);
      });
  }

  private Lesson ensureLesson(Course course, String title, String contentText, int orderIndex) {
    return lessonRepo.findByCourseIdAndTitle(course.getId(), title)
      .orElseGet(() -> {
        Lesson l = new Lesson();
        l.setCourse(course);
        l.setTitle(title);
        l.setContentText(contentText);
        l.setOrderIndex(orderIndex);
        return lessonRepo.save(l);
      });
  }

  private void ensureEnrollment(Course course, User student) {
    enrollmentRepo.findByCourseIdAndStudentId(course.getId(), student.getId())
      .orElseGet(() -> {
        var e = new com.example.lms.domain.Enrollment();
        e.setCourse(course);
        e.setStudent(student);
        return enrollmentRepo.save(e);
      });
  }
}
