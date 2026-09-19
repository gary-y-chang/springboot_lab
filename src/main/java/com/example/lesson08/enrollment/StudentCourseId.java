package com.example.lesson08.enrollment;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * 投影片 B.6 第一步：複合主鍵類別。
 * 三個必要條件：@Embeddable、預設建構子、equals / hashCode。
 * Hibernate 的一級快取用 Map 存實體，Key 就是主鍵物件；
 * 若不重寫 equals / hashCode，Java 會比較記憶體位址，快取永遠命中不了。
 */
@Embeddable
public class StudentCourseId implements Serializable {

    private Long studentId;
    private Long courseId;

    public StudentCourseId() { }

    public StudentCourseId(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public Long getStudentId() { return studentId; }
    public Long getCourseId() { return courseId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentCourseId that = (StudentCourseId) o;
        return Objects.equals(studentId, that.studentId)
                && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }
}
