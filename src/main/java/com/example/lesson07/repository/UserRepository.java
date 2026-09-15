package com.example.lesson07.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.lesson07.entity.User;
import java.util.List;

/** 範例一：標準用法（@Repository 選填，但能提升可讀性）。 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 自動享有內建的 save(), findById(), delete() 功能

    // 自訂方法：根據 Email 尋找使用者
    User findByEmail(String email);

    List<User> findByName(String name);

    long countByName(String name);
}
