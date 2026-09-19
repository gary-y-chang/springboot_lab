package com.example.lesson08.profile.dto;

import com.example.lesson08.profile.User;

import java.time.LocalDateTime;

/** 一對一：把 User 與 UserProfile 攤平成一個 DTO，避免雙向關聯序列化無窮迴圈 */
public record UserDto(Long id, String username, String lastName, String email,
                      String status, LocalDateTime createTime, String bio) {

    public static UserDto from(User u) {
        return new UserDto(u.getId(), u.getUsername(), u.getLastName(), u.getEmail(),
                u.getStatus(), u.getCreateTime(),
                u.getProfile() == null ? null : u.getProfile().getBio());
    }
}
