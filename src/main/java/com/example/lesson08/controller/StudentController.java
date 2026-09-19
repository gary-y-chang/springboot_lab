package com.example.lesson08.controller;

import com.example.lesson08.enrollment.EnrollmentService;
import com.example.lesson08.enrollment.dto.StudentDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投影片 B.6：中間表升格為實體（StudentCourse）後的學生端操作。
 * 選課與查詢成績單在 EnrollmentController。
 */
@RestController
@RequestMapping("/lesson08/students")
public class StudentController {

    private final EnrollmentService enrollmentService;

    public StudentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public List<StudentDto> list() {
        return enrollmentService.listStudents();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDto create(@RequestBody StudentRequest request) {
        return enrollmentService.createStudent(request.name());
    }

    /** cascade = ALL：連帶刪除該生所有選課紀錄 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        enrollmentService.deleteStudent(id);
    }

    /** 修改中間表的額外欄位 grade */
    @PutMapping("/{id}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGrade(@PathVariable Long id, @PathVariable Long courseId,
                            @RequestParam Integer grade) {
        enrollmentService.updateGrade(id, courseId, grade);
    }

    /** 退選：orphanRemoval 刪除中間表那一列 */
    @DeleteMapping("/{id}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void drop(@PathVariable Long id, @PathVariable Long courseId) {
        enrollmentService.drop(id, courseId);
    }

    public record StudentRequest(String name) { }
}
