package com.example.lesson07.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * B.1 Entity 定義 / B.2 欄位映射。
 *
 * 三條規範：必須有 @Id 主鍵、必須有無參數建構子、類別不可為 final。
 */
@Entity                        // 告訴 JPA 管理此類別
@Table(name = "employees")     // 指定對應資料表（不加則用類別名稱）
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 由資料庫自動遞增
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private int age;

    @Column(unique = true, length = 120)
    private String email;

    @Enumerated(EnumType.STRING) // 存字串比存序號安全
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    private LocalDate hiredOn;   // LocalDate 已自動對應，不需 @Temporal

    protected Employee() {
        // JPA 需要的無參數建構子（public 或 protected，不可 private）
    }

    public Employee(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.hiredOn = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public LocalDate getHiredOn() {
        return hiredOn;
    }

    public void setHiredOn(LocalDate hiredOn) {
        this.hiredOn = hiredOn;
    }
}
