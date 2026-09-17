package com.example.lesson07.dto;

import com.example.lesson07.entity.User;

/** 回傳給前端的資料形狀，避免直接曝露 Entity。 */
public record UserDto(Long id, String name, String email) {

    public static UserDto from(User u) {
        return new UserDto(u.getId(), u.getUserName(), u.getEmail());
    }
}
