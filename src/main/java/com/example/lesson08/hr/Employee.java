package com.example.lesson08.hr;

import jakarta.persistence.*;

/**
 * 投影片 B.1 / Lab 1：多對一的「擁有方」，外鍵 dept_id 落在 hr_employees 表。
 * @ManyToOne 預設是 EAGER，這裡手動改成 LAZY（投影片 B.5 注意事項）。
 */
// lesson07 已有同名 Employee 實體與 employees 表，這裡改用 HrEmployee / hr_employees 區分
@Entity(name = "HrEmployee")
@Table(name = "hr_employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private Integer age;

    @Column(length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department dept;

    protected Employee() { }

    public Employee(String name, Integer age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Department getDept() { return dept; }
    public void setDept(Department dept) { this.dept = dept; }
}
