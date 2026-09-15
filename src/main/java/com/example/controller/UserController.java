package com.example.controller;

import com.example.dto.CreateUserRequest;
import com.example.exception.BusinessException;
import com.example.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    // 模擬資料庫
    private static final Map<Integer, String> USERS = Map.of(101, "Alice", 202, "Bob");

    // 這裡不需要寫任何日誌或 try-catch，保持業務邏輯乾淨：
    // 日誌交給 LoggingAspect，例外交給 GlobalExceptionHandler
    @GetMapping("/api/greet")
    public String greetUser(@RequestParam String name) {
        // 模擬業務邏輯
        return "Hello, " + name + "!";
    }

    /** 示範 ResourceNotFoundException -> 404 */
    @GetMapping("/api/users/{id}")
    public Map<String, Object> getUser(@PathVariable Integer id) {
        String name = USERS.get(id);
        if (name == null) {
            throw ResourceNotFoundException.of("User", id);
        }
        return Map.of("id", id, "name", name) ;
    }

    /** 示範 @Valid 驗證失敗 -> 400，以及 BusinessException -> 409 */
    @PostMapping("/api/users")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody CreateUserRequest request) {
        if (USERS.containsValue(request.name())) {
            throw new BusinessException(
                    "使用者 " + request.name() + " 已存在", "USER_ALREADY_EXISTS", HttpStatus.CONFLICT);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", 99L, "name", request.name(), "email", request.email()));
    }

    /** 示範未預期例外（ArithmeticException）-> 500 保底處理 */
    @GetMapping("/api/divide")
    public Map<String, Object> divide(@RequestParam int a, @RequestParam int b) {
        return Map.of("result", a / b);
    }
}
