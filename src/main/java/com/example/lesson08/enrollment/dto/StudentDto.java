package com.example.lesson08.enrollment.dto;

import com.example.lesson08.enrollment.Student;

public record StudentDto(Long id, String name) {

    public static StudentDto from(Student s) {
        return new StudentDto(s.getId(), s.getName());
    }
}
