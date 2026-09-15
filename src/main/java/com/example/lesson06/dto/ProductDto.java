package com.example.lesson06.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Lab 1 ~ 3 參考解：商品資料傳輸物件（含 Bean Validation）。
 * 驗證失敗 -> 400，由第 5 堂的 @RestControllerAdvice 輸出統一錯誤格式。
 */
public record ProductDto(

        Long id,

        @NotBlank(message = "商品名稱不可為空白")
        @Size(max = 60, message = "商品名稱長度不可超過 60 個字")
        String name,

        @NotNull(message = "價格必填")
        @Positive(message = "價格必須大於 0")
        Integer price,

        @NotNull(message = "分類必填")
        Long categoryId) {
}
