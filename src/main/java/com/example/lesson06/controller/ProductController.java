package com.example.lesson06.controller;

import com.example.lesson06.dto.ProductDto;
import com.example.lesson06.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lab 1 參考解：商品管理完整 CRUD。
 * Lab 2 參考解：列表支援 keyword / page / size，並提供依分類查詢。
 * Lab 3 參考解：新增與更新加上 @Valid，驗證失敗回 400。
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    /** GET /products?keyword=筆電&page=0&size=10 */
    @GetMapping
    public List<ProductDto> all(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.findAll(keyword, page, size);
    }

    /** GET /products/1 — 不存在時丟 ResourceNotFoundException -> 404 */
    @GetMapping("/{id}")
    public ProductDto one(@PathVariable Long id) {
        return service.findById(id);
    }

    /** POST /products — 新增成功回 201 Created */
    @PostMapping
    public ResponseEntity<ProductDto> add(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    /** PUT /products/1 — 完全更新，回 200 OK */
    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        return service.update(id, dto);
    }

    /** DELETE /products/1 — 回 204 No Content */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
