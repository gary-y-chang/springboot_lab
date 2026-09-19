package com.example.lesson08.enrollment;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/** 投影片 B.6：課程端同樣改為 @OneToMany，並提供雙向關聯輔助方法 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentCourse> students = new ArrayList<>();

    protected Course() { }

    public Course(String title) {
        this.title = title;
    }

    /** 由 Course 反向建立關聯：兩端都要在記憶體中被賦值 */
    public void addStudent(Student student, Integer grade) {
        StudentCourse sc = new StudentCourse(student, this, grade);
        this.students.add(sc);
        student.getCourses().add(sc);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public List<StudentCourse> getStudents() { return students; }
}
