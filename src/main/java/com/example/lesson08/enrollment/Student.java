package com.example.lesson08.enrollment;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/** 投影片 B.6 第三步：原本的 @ManyToMany 改寫為指向中間表實體的 @OneToMany */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentCourse> courses = new ArrayList<>();

    protected Student() { }

    public Student(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<StudentCourse> getCourses() { return courses; }
}
