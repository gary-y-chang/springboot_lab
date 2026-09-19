package com.example.lesson08.manytomany;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SimpleCourseRepository extends JpaRepository<Course, Long> { }
