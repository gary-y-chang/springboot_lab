package com.example.lesson08.enrollment;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lesson08/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public void enroll(@RequestParam Long studentId,
                       @RequestParam Long courseId,
                       @RequestParam Integer grade) {
        enrollmentService.enroll(studentId, courseId, grade);
    }

    @PostMapping("/from-course")
    public void enrollFromCourse(@RequestParam Long studentId,
                                 @RequestParam Long courseId,
                                 @RequestParam Integer grade) {
        enrollmentService.enrollFromCourse(studentId, courseId, grade);
    }

    @GetMapping("/students/{id}")
    public List<Map<String, Object>> coursesOf(@PathVariable Long id) {
        return enrollmentService.coursesOf(id);
    }

    @GetMapping("/courses/{id}")
    public List<Map<String, Object>> scoreboard(@PathVariable Long id) {
        return enrollmentService.scoreboard(id);
    }
}
