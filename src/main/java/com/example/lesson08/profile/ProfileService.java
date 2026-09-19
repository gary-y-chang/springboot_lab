package com.example.lesson08.profile;

import com.example.exception.ResourceNotFoundException;
import com.example.lesson08.profile.dto.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 注意 Console：User 是 mappedBy 的被動方，即使標了 LAZY，
     * Hibernate 仍得查 user_profiles 才知道 profile 是否為 null，因此每位使用者多一筆 SQL。
     */
    public List<UserDto> list() {
        return toDto(userRepository.findAll());
    }

    public UserDto get(Long id) {
        return UserDto.from(find(id));
    }

    // ---------- 投影片 A.3 / A.4：JPQL vs Native SQL ----------
    public List<UserDto> byStatus(String status, boolean nativeSql) {
        return toDto(nativeSql
                ? userRepository.findByStatusNative(status)
                : userRepository.findByStatus(status));
    }

    public UserDto lookup(String email, String lastName) {
        User user = userRepository.findByEmailAndLastName(email, lastName);
        if (user == null) {
            throw new ResourceNotFoundException("找不到 User，email = " + email + "，lastName = " + lastName);
        }
        return UserDto.from(user);
    }

    public List<UserDto> recent() {
        return toDto(userRepository.findRecentUsersNative());
    }

    // ---------- 投影片 A.1 / A.2：命名查詢 ----------
    public List<UserDto> search(String keyword) {
        return toDto(userRepository.findByUsernameContainingOrderByIdDesc(keyword));
    }

    // ---------- 投影片 B.2：一對一的新增 / 修改 / 刪除 ----------
    @Transactional
    public UserDto create(String username, String lastName, String email, String status, String bio) {
        User user = new User(username, lastName, email, status);
        if (bio != null) {
            user.setProfile(new UserProfile(bio));      // 兩端同時賦值
        }
        return UserDto.from(userRepository.save(user)); // CascadeType.ALL 連帶寫入 user_profiles
    }

    @Transactional
    public UserDto updateBio(Long id, String bio) {
        User user = find(id);
        if (user.getProfile() == null) {
            user.setProfile(new UserProfile(bio));      // 原本沒有 profile：cascade 新增一筆
        } else {
            user.getProfile().setBio(bio);              // 已有 profile：Dirty Checking，不必呼叫 save()
        }
        return UserDto.from(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(find(id));                // cascade 先刪 user_profiles，再刪 users
    }

    private User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }

    private List<UserDto> toDto(List<User> users) {
        return users.stream().map(UserDto::from).toList();
    }
}
