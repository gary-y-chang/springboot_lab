package com.example.lesson06.controller;

import com.example.lesson06.dto.UserRegisterDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 06 · 範例三：在 Controller 啟用驗證。
 * 驗證失敗時 Spring Boot 會阻斷請求、不執行方法本體，
 * 拋出 MethodArgumentNotValidException，前端收到 400 Bad Request。
 * （第 5 堂的 GlobalExceptionHandler 會把它整理成乾淨的欄位對訊息 JSON）
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    // 關鍵點：在 @RequestBody 前面加上 @Valid
    public String registerUser(@Valid @RequestBody UserRegisterDto dto) {
        // 如果執行到這裡，代表資料全部驗證通過！
        return "帳號 " + dto.getUsername() + " 驗證成功，允許註冊！";
    }
}
