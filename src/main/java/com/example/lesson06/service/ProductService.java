package com.example.lesson06.service;

import com.example.exception.ResourceNotFoundException;
import com.example.lesson06.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lab 參考解：以記憶體 Map 模擬資料庫，沿用第 5 堂的三層架構
 * （Controller 只轉呼叫 Service，第 7 堂才換成真正的 JPA Repository）。
 */
@Service
public class ProductService {

    private final Map<Long, ProductDto> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public ProductService() {
        create(new ProductDto(null, "筆電", 32900, 3L));
        create(new ProductDto(null, "機械鍵盤", 2890, 3L));
        create(new ProductDto(null, "咖啡豆", 480, 7L));
    }

    /** Lab 1：查全部。Lab 2：支援關鍵字過濾與分頁。 */
    public List<ProductDto> findAll(String keyword, int page, int size) {
        List<ProductDto> result = new ArrayList<>(store.values());
        if (keyword != null && !keyword.isBlank()) {
            result = result.stream()
                    .filter(p -> p.name().contains(keyword))
                    .toList();
        }
        return paginate(result, page, size);
    }

    /** Lab 2：依分類查詢。 */
    public List<ProductDto> findByCategory(Long categoryId, int page, int size) {
        List<ProductDto> result = store.values().stream()
                .filter(p -> categoryId.equals(p.categoryId()))
                .toList();
        return paginate(result, page, size);
    }

    public ProductDto findById(Long id) {
        ProductDto found = store.get(id);
        if (found == null) {
            throw ResourceNotFoundException.of("Product", id);
        }
        return found;
    }

    public ProductDto create(ProductDto dto) {
        long id = seq.incrementAndGet();
        ProductDto saved = new ProductDto(id, dto.name(), dto.price(), dto.categoryId());
        store.put(id, saved);
        return saved;
    }

    public ProductDto update(Long id, ProductDto dto) {
        findById(id); // 不存在就丟 404
        ProductDto updated = new ProductDto(id, dto.name(), dto.price(), dto.categoryId());
        store.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id);
        store.remove(id);
    }

    private List<ProductDto> paginate(List<ProductDto> source, int page, int size) {
        int from = Math.min(page * size, source.size());
        int to = Math.min(from + size, source.size());
        return source.subList(from, to);
    }
}
