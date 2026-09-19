package com.example.lesson08.manytomany;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/** 投影片 B.3：多對多的被動方，mappedBy 指向 Student 的 courses 集合 */
@Entity(name = "SimpleCourse")
@Table(name = "simple_courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();

    protected Course() { }

    public Course(String title) {
        this.title = title;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Set<Student> getStudents() { return students; }
}
