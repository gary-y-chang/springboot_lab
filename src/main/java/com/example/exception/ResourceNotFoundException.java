package com.example.exception;

import org.springframework.http.HttpStatus;

/**
 * 查無資料例外：繼承 BusinessException，固定回傳 404 Not Found。
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    /**
     * 便利建構子：ResourceNotFoundException.of("User", 99) -> "找不到 User，id = 99"
     */
    public static ResourceNotFoundException of(String resourceName, Object id) {
        return new ResourceNotFoundException("找不到 " + resourceName + "，id = " + id);
    }
}
