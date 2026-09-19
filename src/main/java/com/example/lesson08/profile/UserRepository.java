package com.example.lesson08.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// lesson07 也有 UserRepository，預設 bean 名稱會衝突，故指定專屬名稱
@Repository("profileUserRepository")
public interface UserRepository extends JpaRepository<User, Long> {

    // ===== 投影片 A.3：JPQL（預設，資料庫無關）=====
    @Query("SELECT u FROM ProfileUser u WHERE u.status = :status")
    List<User> findByStatus(@Param("status") String status);

    @Query("SELECT u FROM ProfileUser u WHERE u.email = ?1 AND u.lastName = ?2")
    User findByEmailAndLastName(String email, String lastName);

    // ===== 投影片 A.4：Native SQL（nativeQuery = true）=====
    @Query(value = "SELECT * FROM profile_users WHERE status = :status", nativeQuery = true)
    List<User> findByStatusNative(@Param("status") String status);

    /** MySQL 8 專用語法：綁定特定資料庫，換 DB 需重寫 */
    @Query(value = "SELECT * FROM profile_users WHERE create_time >= NOW() - INTERVAL 1 DAY", nativeQuery = true)
    List<User> findRecentUsersNative();

    // ===== 投影片 A.1 / A.2：命名查詢 =====
    List<User> findByUsernameContainingOrderByIdDesc(String keyword);
}
