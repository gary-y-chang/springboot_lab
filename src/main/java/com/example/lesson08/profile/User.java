package com.example.lesson08.profile;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** 投影片 B.2：一對一的被動方（mappedBy 指向 UserProfile 的 user 屬性） */
// lesson07 已有同名 User 實體與 users 表，這裡改用 ProfileUser / profile_users 區分
@Entity(name = "ProfileUser")
@Table(name = "profile_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(length = 50)
    private String lastName;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String status;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    protected User() { }

    public User(String username, String lastName, String email, String status) {
        this.username = username;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
        if (profile != null) profile.setUser(this);
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public UserProfile getProfile() { return profile; }
}
