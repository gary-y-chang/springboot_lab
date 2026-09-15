package com.example.lesson07.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import com.example.lesson07.entity.User;
import java.util.List;

/** 範例二：自訂擴充用法 —— 實作類別必須手動加上 @Repository，否則 Service 無法注入。 */
@Repository
public class UserCustomRepository {

    @PersistenceContext
    private EntityManager entityManager; // 注入 JPA 原生管理器

    // 手動編寫複雜的動態查詢
    @SuppressWarnings("unchecked")
    public List<User> findUsersByComplexCondition(String keyword) {
        String sql = "SELECT * FROM users WHERE name LIKE :kw OR email LIKE :kw";
        return entityManager.createNativeQuery(sql, User.class)
                            .setParameter("kw", "%" + keyword + "%")
                            .getResultList();
    }
}
