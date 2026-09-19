package com.example.lesson08.manytomany;

import com.example.exception.ResourceNotFoundException;
import com.example.lesson08.manytomany.dto.SimpleCourseDto;
import com.example.lesson08.manytomany.dto.SimpleStudentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SimpleEnrollmentService {

    private final SimpleStudentRepository studentRepository;
    private final SimpleCourseRepository courseRepository;

    public SimpleEnrollmentService(SimpleStudentRepository studentRepository,
                                   SimpleCourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    /** 優化前：1 筆查學生 + 每位學生各 1 筆查課程（N+1） */
    public List<SimpleStudentDto> listStudents() {
        return studentRepository.findAll().stream().map(SimpleStudentDto::from).toList();
    }

    /** 優化後：JOIN FETCH 一次帶回（INNER JOIN，沒選課的學生不會出現） */
    public List<SimpleStudentDto> listStudentsWithCourses() {
        return studentRepository.findAllWithCourses().stream().map(SimpleStudentDto::from).toList();
    }

    public List<SimpleCourseDto> listCourses() {
        return courseRepository.findAll().stream().map(SimpleCourseDto::from).toList();
    }

    @Transactional
    public SimpleStudentDto createStudent(String name) {
        return SimpleStudentDto.from(studentRepository.save(new Student(name)));
    }

    @Transactional
    public SimpleCourseDto createCourse(String title) {
        return SimpleCourseDto.from(courseRepository.save(new Course(title)));
    }

    /** 投影片 B.3：由擁有方 Student 加入課程，Hibernate 寫入中間表 simple_student_courses */
    @Transactional
    public SimpleStudentDto enroll(Long studentId, Long courseId) {
        Student student = findStudent(studentId);
        student.addCourse(findCourse(courseId));   // 兩端同時賦值；Dirty Checking 自動 INSERT 中間表
        return SimpleStudentDto.from(student);
    }

    /** 退選：只刪中間表那一列，學生與課程本身都還在 */
    @Transactional
    public SimpleStudentDto drop(Long studentId, Long courseId) {
        Student student = findStudent(studentId);
        Course course = findCourse(courseId);
        student.getCourses().remove(course);
        course.getStudents().remove(student);
        return SimpleStudentDto.from(student);
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("SimpleStudent", id));
    }

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("SimpleCourse", id));
    }
}
