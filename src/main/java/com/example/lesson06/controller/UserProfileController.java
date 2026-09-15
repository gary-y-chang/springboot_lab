package com.example.lesson06.controller;

import com.example.lesson06.dto.UserResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Lesson 06 · 範例二：Controller 只要正常 return DTO，Jackson 會自動序列化。
 *
 * GET /api/users/me 回傳：
 * {
 *   "id": 101,
 *   "nick_name": "小明",
 *   "registerTime": "2026-09-13 22:17:00"
 * }
 */
@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    @GetMapping("/me")
    public UserResponseDto getMyProfile() {
        // 模擬從資料庫查出資料後，組裝成 DTO
        return new UserResponseDto(
                101L,
                "小明",
                LocalDateTime.now(),
                null,                        // 故意給 null，測試 @JsonInclude 排除效果
                "這是後台備註，前端不該看到"   // 故意給值，測試 @JsonIgnore 隱藏效果
        );
    }
}
