package com.example.lesson08.config;

import com.example.lesson08.enrollment.*;
import com.example.lesson08.hr.Department;
import com.example.lesson08.hr.DepartmentRepository;
import com.example.lesson08.hr.Employee;
import com.example.lesson08.profile.User;
import com.example.lesson08.profile.UserProfile;
import com.example.lesson08.profile.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 啟動時塞入示範資料（資料庫已有資料就跳過），方便直接觀察 Lab 3 的 N+1 */
@Configuration
public class DataSeeder {

    @Bean
    @Transactional
    CommandLineRunner seed(DepartmentRepository departmentRepository,
                           UserRepository userRepository,
                           StudentRepository studentRepository,
                           CourseRepository courseRepository,
                           EnrollmentService enrollmentService) {
        return args -> {
            if (departmentRepository.count() == 0) {
                List<String> deptNames = List.of("研發部", "行銷部", "客服部", "財務部");
                int seq = 1;
                for (String deptName : deptNames) {
                    Department dept = new Department(deptName);
                    for (int i = 0; i < 25; i++) {          // 共 100 名員工，N+1 效果明顯
                        Employee employee = new Employee(
                                "Employee" + seq,
                                22 + (seq % 30),
                                "user" + seq + "@example.com");
                        dept.addEmployee(employee);
                        seq++;
                    }
                    departmentRepository.save(dept);
                }
            }

            if (userRepository.count() == 0) {
                User alice = new User("alice", "Wang", "alice@example.com", "ACTIVE");
                alice.setProfile(new UserProfile("後端工程師"));
                User bob = new User("bob", "Chen", "bob@example.com", "INACTIVE");
                bob.setProfile(new UserProfile("前端工程師"));
                userRepository.saveAll(List.of(alice, bob));
            }

            if (studentRepository.count() == 0) {
                Student s1 = studentRepository.save(new Student("小明"));
                Student s2 = studentRepository.save(new Student("小華"));
                Course c1 = courseRepository.save(new Course("Spring Boot 實戰"));
                Course c2 = courseRepository.save(new Course("資料庫設計"));

                enrollmentService.enroll(s1.getId(), c1.getId(), 95);
                enrollmentService.enroll(s1.getId(), c2.getId(), 88);
                enrollmentService.enrollFromCourse(s2.getId(), c1.getId(), 72);
            }
        };
    }
}
