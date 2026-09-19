package com.example.lesson08.manytomany.dto;

import com.example.lesson08.manytomany.Course;
import com.example.lesson08.manytomany.Student;

import java.util.List;

public record SimpleStudentDto(Long id, String name, List<String> courses) {

    public static SimpleStudentDto from(Student s) {
        return new SimpleStudentDto(s.getId(), s.getName(),
                s.getCourses().stream().map(Course::getTitle).sorted().toList());
    }
}
