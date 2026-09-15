package com.example.lesson06.dto;

/**
 * Lesson 06 範例：圖書資料傳輸物件。
 * @RequestBody（JSON）與 @ModelAttribute（表單）都綁到這個型別。
 * 注意：@ModelAttribute 需要無參數建構子 + setter，因此這裡用一般類別而非 record。
 */
public class BookDto {

    private String title;
    private Integer price;

    public BookDto() {
    }

    public BookDto(String title, Integer price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
