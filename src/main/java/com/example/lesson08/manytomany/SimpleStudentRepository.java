package com.example.lesson08.manytomany;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SimpleStudentRepository extends JpaRepository<Student, Long> {

    /** JPQL 用的是實體名稱 SimpleStudent（見 @Entity(name = ...)） */
    @Query("SELECT DISTINCT s FROM SimpleStudent s JOIN FETCH s.courses")
    List<Student> findAllWithCourses();
}
