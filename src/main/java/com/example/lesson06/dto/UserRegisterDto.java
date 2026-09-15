package com.example.lesson06.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Lesson 06 · 範例三：在 DTO 宣告驗證規則，並自訂錯誤訊息。
 * 需要 spring-boot-starter-validation 依賴。
 */
public class UserRegisterDto {

    // 1. 不能為空，且修剪空白後長度必須大於 0
    @NotBlank(message = "使用者名稱不能為空")
    @Size(min = 2, max = 20, message = "使用者名稱長度必須在 2 到 20 個字之間")
    private String username;

    // 2. 密碼強度檢查
    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, message = "密碼長度至少需要 6 個字")
    private String password;

    // 3. 必須符合標準 Email 格式
    @NotBlank(message = "Email 不能為空")
    @Email(message = "Email 格式不正確")
    private String email;

    // 4. 數字數值限制
    @Min(value = 18, message = "未滿 18 歲無法註冊")
    @Max(value = 100, message = "請輸入合法的年齡")
    private Integer age;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
