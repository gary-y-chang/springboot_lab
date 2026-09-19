package com.example.lesson08.hr;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 投影片 B.1 / Lab 1：一對多的「被動方（非維護端）」。
 * mappedBy 的值 = Employee 類別中對應的屬性名稱（dept）。
 */
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "dept",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    protected Department() { }

    public Department(String name) {
        this.name = name;
    }

    /** 雙向關聯輔助方法：兩端都要在記憶體中被賦值 */
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDept(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDept(null);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Employee> getEmployees() { return employees; }
}
