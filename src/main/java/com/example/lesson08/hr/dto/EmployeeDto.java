package com.example.lesson08.hr.dto;

import com.example.lesson08.hr.Employee;

/**
 * 投影片 B.5：雙向關聯直接轉 JSON 會 StackOverflowError，
 * 最推薦的做法是用 DTO 傳輸，不要直接暴露 Entity。
 */
public record EmployeeDto(Long id, String name, Integer age, String email, String deptName) {

    public static EmployeeDto from(Employee e) {
        return new EmployeeDto(
                e.getId(), e.getName(), e.getAge(), e.getEmail(),
                e.getDept() == null ? null : e.getDept().getName());
    }
}
