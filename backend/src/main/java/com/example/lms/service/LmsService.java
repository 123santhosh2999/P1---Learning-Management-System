package com.example.lms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.lms.domain.*;
import com.example.lms.dto.CourseDtos;
import com.example.lms.dto.LessonDtos;
import com.example.lms.dto.ProgressDtos;
import com.example.lms.repo.*;

@Service
public class LmsService {

  private final UserRepository userRepo;
  private final CourseRepository courseRepo;
  private final LessonRepository lessonRepo;
  private final EnrollmentRepository enrollmentRepo;
  private final ProgressRepository progressRepo;
  private final UploadService uploadService;

  public LmsService(
    UserRepository userRepo,
    CourseRepository courseRepo,
    LessonRepository lessonRepo,
    EnrollmentRepository enrollmentRepo,
    ProgressRepository progressRepo,
    UploadService uploadService
  ) {
    this.userRepo = userRepo;
    this.courseRepo = courseRepo;
    this.lessonRepo = lessonRepo;
    this.enrollmentRepo = enrollmentRepo;
    this.progressRepo = progressRepo;
    this.uploadService = uploadService;
  }

  // Admin

  @Transactional(readOnly = true)
  public List<User> listUsers() {
    return userRepo.findAll();
  }

  @Transactional
  public User updateUserRole(Long userId, Role role) {
    User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    user.setRole(role);
    return userRepo.save(user);
  }

  @Transactional
  public void deleteUser(Long userId) {
    if (!userRepo.existsById(userId)) throw new IllegalArgumentException("User not found");
    userRepo.deleteById(userId);
  }

  @Transactional(readOnly = true)
  public List<Course> listCoursesForAdmin(CourseStatus status) {
    if (status == null) return courseRepo.findAll();
    return courseRepo.findByStatusOrderByIdDesc(status);
  }

  @Transactional
  public Course setCourseStatus(Long courseId, CourseStatus status) {
    Course course = courseRepo.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
    course.setStatus(status);
    return courseRepo.save(course);
  }

  @Transactional
  public void deleteCourse(Long courseId) {
    if (!courseRepo.existsById(courseId)) throw new IllegalArgumentException("Course not found");
    courseRepo.deleteById(courseId);
  }

  // Instructor

  @Transactional
  public Course createCourse(Long instructorId, CourseDtos.CreateCourseRequest req) {
    User instructor = userRepo.findById(instructorId).orElseThrow(() -> new IllegalArgumentException("User not found"));

    Course course = new Course();
    course.setTitle(req.title());
    course.setDescription(req.description());
    course.setStatus(CourseStatus.PENDING);
    course.setInstructor(instructor);

    return courseRepo.save(course);
  }

  @Transactional
  public Course updateCourse(Long instructorId, Long courseId, CourseDtos.UpdateCourseRequest req, boolean isAdmin) {
    Course course = courseRepo.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

    if (!isAdmin && !course.getInstructor().getId().equals(instructorId)) {
      throw new IllegalArgumentException("Forbidden");
    }

    if (req.title() != null) course.setTitle(req.title());
    if (req.description() != null) course.setDescription(req.description());

    course.setStatus(CourseStatus.PENDING);
    return courseRepo.save(course);
  }

  @Transactional
  public Lesson addLesson(Long instructorId, Long courseId, LessonDtos.CreateLessonRequest req, MultipartFile media, boolean isAdmin) {
    Course course = courseRepo.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

    if (!isAdmin && !course.getInstructor().getId().equals(instructorId)) {
      throw new IllegalArgumentException("Forbidden");
    }

    Lesson lesson = new Lesson();
    lesson.setCourse(course);
    lesson.setTitle(req.title());
    lesson.setContentText(req.contentText());
    lesson.setVideoUrl(req.videoUrl());
    lesson.setPdfUrl(req.pdfUrl());
    lesson.setOrderIndex(req.orderIndex() != null ? req.orderIndex() : 0);
    lesson.setMediaPath(uploadService.save(media));

    return lessonRepo.save(lesson);
  }

  @Transactional(readOnly = true)
  public List<Enrollment> listCourseEnrollments(Long instructorId, Long courseId, boolean isAdmin) {
    Course course = courseRepo.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

    if (!isAdmin && !course.getInstructor().getId().equals(instructorId)) {
      throw new IllegalArgumentException("Forbidden");
    }

    return enrollmentRepo.findByCourseId(courseId);
  }

  // Student

  @Transactional(readOnly = true)
  public List<Course> browseApprovedCourses() {
    return courseRepo.findByStatusOrderByIdDesc(CourseStatus.APPROVED);
  }

  @Transactional
  public Enrollment enroll(Long studentId, Long courseId) {
    Course course = courseRepo.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
    if (course.getStatus() != CourseStatus.APPROVED) throw new IllegalArgumentException("Course not available");

    User student = userRepo.findById(studentId).orElseThrow(() -> new IllegalArgumentException("User not found"));

    var existing = enrollmentRepo.findByCourseIdAndStudentId(courseId, studentId);
    if (existing.isPresent()) throw new IllegalArgumentException("Already enrolled");

    Enrollment e = new Enrollment();
    e.setCourse(course);
    e.setStudent(student);
    return enrollmentRepo.save(e);
  }

  @Transactional(readOnly = true)
  public List<Enrollment> myEnrollments(Long studentId) {
    return enrollmentRepo.findByStudentId(studentId);
  }

  @Transactional(readOnly = true)
  public List<Lesson> courseLessonsForStudent(Long studentId, Long courseId) {
    var enrolled = enrollmentRepo.findByCourseIdAndStudentId(courseId, studentId);
    if (enrolled.isEmpty()) throw new IllegalArgumentException("Enroll to access lessons");

    return lessonRepo.findByCourseIdOrderByOrderIndexAscIdAsc(courseId);
  }

  @Transactional
  public Progress updateLessonProgress(Long studentId, Long lessonId, ProgressStatus status) {
    Lesson lesson = lessonRepo.findById(lessonId).orElseThrow(() -> new IllegalArgumentException("Lesson not found"));
    Long courseId = lesson.getCourse().getId();

    var enrolled = enrollmentRepo.findByCourseIdAndStudentId(courseId, studentId);
    if (enrolled.isEmpty()) throw new IllegalArgumentException("Enroll to access this lesson");

    Progress progress = progressRepo.findByStudentIdAndLessonId(studentId, lessonId).orElse(null);
    if (progress == null) {
      User student = userRepo.findById(studentId).orElseThrow(() -> new IllegalArgumentException("User not found"));
      progress = new Progress();
      progress.setStudent(student);
      progress.setLesson(lesson);
    }

    progress.setStatus(status);
    return progressRepo.save(progress);
  }

  @Transactional(readOnly = true)
  public ProgressDtos.ProgressSummaryResponse progressSummary(Long studentId, Long courseId) {
    var enrolled = enrollmentRepo.findByCourseIdAndStudentId(courseId, studentId);
    if (enrolled.isEmpty()) throw new IllegalArgumentException("Enroll to access progress");

    int total = lessonRepo.findByCourseIdOrderByOrderIndexAscIdAsc(courseId).size();
    int completed = (int) progressRepo.findByStudentIdAndLessonCourseId(studentId, courseId)
      .stream()
      .filter(p -> p.getStatus() == ProgressStatus.COMPLETED)
      .count();

    return new ProgressDtos.ProgressSummaryResponse(total, completed);
  }
}
