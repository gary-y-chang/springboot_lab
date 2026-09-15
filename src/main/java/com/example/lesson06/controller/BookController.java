package com.example.lesson06.controller;

import com.example.lesson06.dto.BookDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 06 · 範例一：4 種常見的接收參數方式。
 * Mapping 是「地址」，Parameters 是「信件包裹裡的內容」。
 */
@RestController
@RequestMapping("/books") // 基礎路徑：所有關於書的 API 都以 /books 開頭
public class BookController {

    // 1. PathVariable (路徑變數) -> 用於定位特定資源
    // GET http://localhost:8080/books/957
    @GetMapping("/{id}")
    public String getBookById(@PathVariable Long id) {
        return "正在讀取 ID 為 " + id + " 的書籍資料";
    }

    // 2. RequestParam (查詢參數) -> 用於過濾、搜尋、分頁
    // GET http://localhost:8080/books/search?keyword=Java&limit=10
    @GetMapping("/search")
    public String searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int limit) { // limit 若沒傳，預設為 5
        return "搜尋關鍵字: " + keyword + "，限制顯示筆數: " + limit;
    }

    // 3. RequestBody (請求主體) -> 接收 JSON 物件（通常用於 POST/PUT）
    // POST http://localhost:8080/books
    // 請求體(JSON)：{ "title": "Spring 入門", "price": 500 }
    @PostMapping
    public String createBook(@RequestBody BookDto bookDto) {
        return "成功新增書籍！書名：" + bookDto.getTitle() + "，價格：" + bookDto.getPrice();
    }

    // 4. ModelAttribute (表單資料) -> 傳統 HTML 表單 (x-www-form-urlencoded)
    // POST http://localhost:8080/books/form（資料帶在 Form Data 裡）
    @PostMapping("/form")
    public String createBookViaForm(@ModelAttribute BookDto bookDto) {
        return "從表單接收到書名：" + bookDto.getTitle();
    }
}
