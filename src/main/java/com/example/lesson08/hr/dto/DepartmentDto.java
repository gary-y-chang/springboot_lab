package com.example.lesson08.hr.dto;

import com.example.lesson08.hr.Department;

import java.util.List;

public record DepartmentDto(Long id, String name, List<EmployeeDto> employees) {

    public static DepartmentDto from(Department d) {
        return new DepartmentDto(d.getId(), d.getName(),
                d.getEmployees().stream().map(EmployeeDto::from).toList());
    }
}
