package com.example.lesson07.dto;

import com.example.lesson07.entity.Employee;

/** 回傳給前端的資料形狀，避免直接曝露 Entity。 */
public record EmployeeDto(Long id, String name, int age, String email, String status) {

    public static EmployeeDto from(Employee e) {
        return new EmployeeDto(e.getId(), e.getName(), e.getAge(), e.getEmail(),
                e.getStatus().name());
    }
}
