package com.example.lesson08.controller;

import com.example.lesson08.enrollment.EnrollmentService;
import com.example.lesson08.enrollment.dto.CourseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 投影片 B.6：課程基本資料（選課與課程成績排行在 EnrollmentController） */
@RestController
@RequestMapping("/lesson08/courses")
public class CourseController {

    private final EnrollmentService enrollmentService;

    public CourseController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public List<CourseDto> list() {
        return enrollmentService.listCourses();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDto create(@RequestBody CourseRequest request) {
        return enrollmentService.createCourse(request.title());
    }

    public record CourseRequest(String title) { }
}
