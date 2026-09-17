package com.example.lesson07.repository;

import com.example.lesson07.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Lab 1 參考解：一行介面即完成資料層。
 *
 * @Repository 在此為選填（Spring Data JPA 會自動註冊代理實作），
 * 加上它只是為了團隊可讀性。
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 內建即有：save / saveAll / findById / findAll / existsById / count
    //           deleteById / delete / deleteAllById / deleteAll
    //           findAll(Pageable) 分頁與排序

    // 方法名稱查詢（Query Methods）：Spring 依名稱解析並生成 SQL
    Optional<Employee> findByEmail(String email);

    List<Employee> findByName(String name);

    List<Employee> findByNameAndEmail(String name, String email); 

    List<Employee> findByAgeGreaterThanEqual(int age); 

    long countByStatus(com.example.lesson07.entity.EmployeeStatus status);
}
