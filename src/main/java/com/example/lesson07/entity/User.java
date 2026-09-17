package com.example.lesson07.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** 投影片「簡單程式碼範例」的 User。 */
@Entity                      // 告訴 JPA 管理此類別
@Table(name = "users")       // 指定對應資料表
public class User {

    @Id                      // 定義主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String email;

    public User() {          // 無參數建構子
    }

    public User(String name, String email) {
        this.userName = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
