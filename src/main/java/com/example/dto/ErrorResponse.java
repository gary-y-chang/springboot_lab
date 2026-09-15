package com.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 統一的錯誤回應格式，所有 API 發生例外時都回傳這個結構。
 *
 * <pre>
 * {
 *   "timestamp": "2026-09-12T11:15:30.123",
 *   "status": 404,
 *   "error": "Not Found",
 *   "code": "RESOURCE_NOT_FOUND",
 *   "message": "找不到 User，id = 99",
 *   "path": "/api/users/99",
 *   "fieldErrors": [ { "field": "name", "message": "姓名不可為空白" } ]
 * }
 * </pre>
 *
 * fieldErrors 為 null 時不會出現在 JSON 中（@JsonInclude NON_NULL）。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<FieldError> fieldErrors) {

    /** 單一欄位的驗證失敗訊息 */
    public record FieldError(String field, Object rejectedValue, String message) {
    }

    public static ErrorResponse of(int status, String error, String code, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, code, message, path, null);
    }

    public static ErrorResponse of(int status, String error, String code, String message, String path,
                                   List<FieldError> fieldErrors) {
        return new ErrorResponse(LocalDateTime.now(), status, error, code, message, path, fieldErrors);
    }
}
