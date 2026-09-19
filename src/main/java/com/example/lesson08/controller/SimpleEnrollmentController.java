package com.example.lesson08.controller;

import com.example.lesson08.manytomany.SimpleEnrollmentService;
import com.example.lesson08.manytomany.dto.SimpleCourseDto;
import com.example.lesson08.manytomany.dto.SimpleStudentDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 投影片 B.3：單純的 @ManyToMany（中間表 simple_student_courses 只有雙邊外鍵） */
@RestController
@RequestMapping("/lesson08/simple")
public class SimpleEnrollmentController {

    private final SimpleEnrollmentService service;

    public SimpleEnrollmentController(SimpleEnrollmentService service) {
        this.service = service;
    }

    /** N+1：每位學生各多一筆查課程的 SQL */
    @GetMapping("/students")
    public List<SimpleStudentDto> students() {
        return service.listStudents();
    }

    /** JOIN FETCH：只剩 1 筆 SQL */
    @GetMapping("/students/with-courses")
    public List<SimpleStudentDto> studentsWithCourses() {
        return service.listStudentsWithCourses();
    }

    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleStudentDto createStudent(@RequestBody StudentRequest request) {
        return service.createStudent(request.name());
    }

    /** 從被動方 Course 反查選課學生 */
    @GetMapping("/courses")
    public List<SimpleCourseDto> courses() {
        return service.listCourses();
    }

    @PostMapping("/courses")
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleCourseDto createCourse(@RequestBody CourseRequest request) {
        return service.createCourse(request.title());
    }

    @PostMapping("/students/{studentId}/courses/{courseId}")
    public SimpleStudentDto enroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        return service.enroll(studentId, courseId);
    }

    @DeleteMapping("/students/{studentId}/courses/{courseId}")
    public SimpleStudentDto drop(@PathVariable Long studentId, @PathVariable Long courseId) {
        return service.drop(studentId, courseId);
    }

    public record StudentRequest(String name) { }

    public record CourseRequest(String title) { }
}
