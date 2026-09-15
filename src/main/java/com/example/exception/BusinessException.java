package com.example.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 業務邏輯例外：用於「可預期」的錯誤（例如餘額不足、帳號已存在）。
 * 自帶錯誤代碼與 HTTP 狀態，由 GlobalExceptionHandler 統一轉成 JSON 回應。
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 給前端辨識用的錯誤代碼，例如 USER_ALREADY_EXISTS */
    private final String code;

    /** 對應的 HTTP 狀態碼，預設 400 Bad Request */
    private final HttpStatus status;

    public BusinessException(String message) {
        this(message, "BUSINESS_ERROR", HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message, String code) {
        this(message, code, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
