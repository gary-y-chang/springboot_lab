package com.example.lesson08.hr;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// lesson07 也有 EmployeeRepository，預設 bean 名稱會衝突，故指定專屬名稱
@Repository("hrEmployeeRepository")
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ===== Lab 1：查某部門的所有員工 =====
    List<Employee> findByDeptId(Long deptId);
    List<Employee> findByDeptName(String deptName);

    // ===== Lab 2.1：依姓名關鍵字模糊查詢 =====
    List<Employee> findByNameContaining(String keyword);

    // ===== Lab 2.2：查年齡區間的員工 =====
    List<Employee> findByAgeBetween(int lo, int hi);

    // ===== Lab 2.4：結果依年齡遞減排序（可與其他關鍵字組合）=====
    List<Employee> findByAgeBetweenOrderByAgeDesc(int lo, int hi);
    List<Employee> findByNameContainingOrderByAgeDesc(String keyword);

    // ===== Lab 2.3：用 @Query (JPQL) 依部門名稱查員工，並依年齡遞減 =====
    @Query("SELECT e FROM HrEmployee e WHERE e.dept.name = :dn ORDER BY e.age DESC")
    List<Employee> byDept(@Param("dn") String dn);

    // 位置參數寫法（投影片 A.3）
    @Query("SELECT e FROM HrEmployee e WHERE e.email = ?1 AND e.name = ?2")
    Employee findByEmailAndName(String email, String name);

    // 原生 SQL 寫法（投影片 A.4）：操作的是資料表與欄位
    @Query(value = "SELECT * FROM hr_employees WHERE age >= :age", nativeQuery = true)
    List<Employee> findOlderThanNative(@Param("age") int age);

    // ===== Lab 3：JOIN FETCH 一次帶回部門，N+1 → 1 筆 SQL =====
    @Query("SELECT e FROM HrEmployee e JOIN FETCH e.dept")
    List<Employee> findAllWithDept();

    // ===== Lab 3 另一解：@EntityGraph =====
    @EntityGraph(attributePaths = "dept")
    List<Employee> findAllBy();
}
