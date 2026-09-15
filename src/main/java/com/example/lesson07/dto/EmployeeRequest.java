package com.example.lesson07.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record EmployeeRequest(
        @NotBlank String name,
        @Min(15) int age,
        @Email String email) {
}
