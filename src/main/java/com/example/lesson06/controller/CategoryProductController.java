package com.example.lesson06.controller;

import com.example.lesson06.dto.ProductDto;
import com.example.lesson06.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lab 2 參考解：巢狀資源路徑 — 路徑變數與查詢參數並用。
 * GET /categories/3/products?page=0&size=10
 */
@RestController
public class CategoryProductController {

    private final ProductService service;

    public CategoryProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/categories/{categoryId}/products")
    public List<ProductDto> byCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.findByCategory(categoryId, page, size);
    }
}
