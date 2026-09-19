package com.example.lesson08.enrollment;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 投影片 B.6 第二步：中間表升格為獨立實體，才能放額外欄位（成績、選課時間）。
 * @MapsId 把 @ManyToOne 的外鍵對應到複合主鍵中的欄位。
 */
@Entity
@Table(name = "student_courses")
public class StudentCourse {

    @EmbeddedId
    private StudentCourseId id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    // --- 額外欄位：這正是 @ManyToMany 做不到的事 ---
    private Integer grade;

    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt = LocalDateTime.now();

    protected StudentCourse() { }

    public StudentCourse(Student student, Course course, Integer grade) {
        this.student = student;
        this.course = course;
        this.grade = grade;
        this.id = new StudentCourseId(student.getId(), course.getId());
    }

    public StudentCourseId getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
}
