package com.example.lesson08.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, StudentCourseId> {

    @Query("SELECT sc FROM StudentCourse sc JOIN FETCH sc.course WHERE sc.student.id = :sid")
    List<StudentCourse> findByStudentId(@Param("sid") Long studentId);

    @Query("SELECT sc FROM StudentCourse sc JOIN FETCH sc.student WHERE sc.course.id = :cid ORDER BY sc.grade DESC")
    List<StudentCourse> findByCourseIdOrderByGradeDesc(@Param("cid") Long courseId);
}
