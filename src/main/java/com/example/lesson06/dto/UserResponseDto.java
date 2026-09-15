package com.example.lesson06.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Lesson 06 · 範例二：用 Jackson 註解控制回傳給前端的 JSON 樣式。
 * 不直接把資料庫 Entity 丟給前端，而是用專門的回應 DTO。
 */
// 如果欄位是 null，就不出現在 JSON 中（精簡體積）
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private Long id;

    // 1. 改變前端看到的 Key 名稱：由大寫駝峰轉為底線型態
    @JsonProperty("nick_name")
    private String nickname;

    // 2. 格式化時間：將 Java 的時間物件轉成漂亮的字串
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime registerTime;

    // 3. 故意保留測試 null 值的欄位
    private String secondaryEmail;

    // 4. 絕對不能給前端看見的敏感隱私，直接隱藏
    @JsonIgnore
    private String internalSystemNote;

    public UserResponseDto(Long id, String nickname, LocalDateTime registerTime,
                           String secondaryEmail, String internalSystemNote) {
        this.id = id;
        this.nickname = nickname;
        this.registerTime = registerTime;
        this.secondaryEmail = secondaryEmail;
        this.internalSystemNote = internalSystemNote;
    }

    // Jackson 序列化時必須要有 Getter 方法
    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public String getInternalSystemNote() {
        return internalSystemNote;
    }
}
