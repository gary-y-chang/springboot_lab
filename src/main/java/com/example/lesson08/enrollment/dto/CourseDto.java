package com.example.lesson08.enrollment.dto;

import com.example.lesson08.enrollment.Course;

public record CourseDto(Long id, String title) {

    public static CourseDto from(Course c) {
        return new CourseDto(c.getId(), c.getTitle());
    }
}
