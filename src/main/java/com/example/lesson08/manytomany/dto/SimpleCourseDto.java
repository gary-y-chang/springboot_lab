package com.example.lesson08.manytomany.dto;

import com.example.lesson08.manytomany.Course;
import com.example.lesson08.manytomany.Student;

import java.util.List;

public record SimpleCourseDto(Long id, String title, List<String> students) {

    public static SimpleCourseDto from(Course c) {
        return new SimpleCourseDto(c.getId(), c.getTitle(),
                c.getStudents().stream().map(Student::getName).sorted().toList());
    }
}
