package com.example.lesson08.enrollment;

import com.example.exception.ResourceNotFoundException;
import com.example.lesson08.enrollment.dto.CourseDto;
import com.example.lesson08.enrollment.dto.StudentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentCourseRepository studentCourseRepository;

    public EnrollmentService(StudentRepository studentRepository,
                             CourseRepository courseRepository,
                             StudentCourseRepository studentCourseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    /** 投影片 B.6：由 Student 端儲存一筆帶額外欄位的關聯 */
    @Transactional
    public void enroll(Long studentId, Long courseId, Integer grade) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        StudentCourse sc = new StudentCourse(student, course, grade);
        student.getCourses().add(sc);

        studentRepository.save(student);   // CascadeType.ALL 自動寫入中間表
    }

    /** 投影片 B.6 延伸：反過來由 Course 端儲存，一樣可行 */
    @Transactional
    public void enrollFromCourse(Long studentId, Long courseId, Integer grade) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        course.addStudent(student, grade);
        // course 已是 managed 狀態：交易提交時 flush 會沿 CascadeType.ALL 自動寫入中間表。
        // 不要呼叫 courseRepository.save(course)：它對既有實體走 merge，會複製出另一個 StudentCourse，
        // 與 student.getCourses() 裡的原物件主鍵相同 → NonUniqueObjectException。
    }

    public List<Map<String, Object>> coursesOf(Long studentId) {
        return studentCourseRepository.findByStudentId(studentId).stream()
                .map(sc -> Map.<String, Object>of(
                        "course", sc.getCourse().getTitle(),
                        "grade", sc.getGrade(),
                        "enrolledAt", sc.getEnrolledAt()))
                .toList();
    }

    public List<Map<String, Object>> scoreboard(Long courseId) {
        return studentCourseRepository.findByCourseIdOrderByGradeDesc(courseId).stream()
                .map(sc -> Map.<String, Object>of(
                        "student", sc.getStudent().getName(),
                        "grade", sc.getGrade()))
                .toList();
    }

    // ---------- 學生 / 課程基本資料 ----------
    public List<StudentDto> listStudents() {
        return studentRepository.findAll().stream().map(StudentDto::from).toList();
    }

    @Transactional
    public StudentDto createStudent(String name) {
        return StudentDto.from(studentRepository.save(new Student(name)));
    }

    public List<CourseDto> listCourses() {
        return courseRepository.findAll().stream().map(CourseDto::from).toList();
    }

    @Transactional
    public CourseDto createCourse(String title) {
        return CourseDto.from(courseRepository.save(new Course(title)));
    }

    // ---------- 中間表實體的修改 / 刪除 ----------
    /** 用複合主鍵查出中間表實體，改成績靠 Dirty Checking，不必呼叫 save() */
    @Transactional
    public void updateGrade(Long studentId, Long courseId, Integer grade) {
        StudentCourse sc = studentCourseRepository.findById(new StudentCourseId(studentId, courseId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "找不到選課紀錄，studentId = " + studentId + "，courseId = " + courseId));
        sc.setGrade(grade);
    }

    /** 從 Student 的集合移除，orphanRemoval = true 會自動 DELETE 中間表那一列 */
    @Transactional
    public void drop(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Student", studentId));
        boolean removed = student.getCourses().removeIf(sc -> sc.getCourse().getId().equals(courseId));
        if (!removed) {
            throw new ResourceNotFoundException(
                    "找不到選課紀錄，studentId = " + studentId + "，courseId = " + courseId);
        }
    }

    /** cascade = ALL：刪學生時連帶刪掉他所有的選課紀錄 */
    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Student", studentId));
        studentRepository.delete(student);
    }
}
