package com.example.lesson08.manytomany;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * 投影片 B.3：單純的多對多（中間表只有雙邊主鍵）擁有方。
 * 註：本專案同時保留「中間表有額外欄位」的版本（com.example.lesson08.enrollment），
 * 兩者類別名稱相同，因此這裡用 @Entity(name = "SimpleStudent") 區分 JPA 實體名稱。
 */
@Entity(name = "SimpleStudent")
@Table(name = "simple_students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "simple_student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    protected Student() { }

    public Student(String name) {
        this.name = name;
    }

    public void addCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Set<Course> getCourses() { return courses; }
}
