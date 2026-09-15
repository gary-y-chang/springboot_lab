package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 建立使用者的請求 Body，用來示範 @Valid 驗證失敗時的全域例外處理。  Data Transfer Object
 */
public record CreateUserRequest(

        @NotBlank(message = "姓名不可為空白")
        String name,

        @NotBlank(message = "Email 不可為空白")
        @Email(message = "Email 格式不正確")
        String email,

        @NotNull(message = "年齡必填")
        @Min(value = 0, message = "年齡不可小於 0")
        @Max(value = 150, message = "年齡不可大於 150")
        Integer age) {
}
