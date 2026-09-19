package com.example.lesson08.profile;

import jakarta.persistence.*;

/** 投影片 B.2：一對一的擁有方，外鍵 user_id 落在 user_profiles 表 */
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String bio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected UserProfile() { }

    public UserProfile(String bio) {
        this.bio = bio;
    }

    public Long getId() { return id; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
