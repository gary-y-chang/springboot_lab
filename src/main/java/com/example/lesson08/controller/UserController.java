package com.example.lesson08.controller;

import com.example.lesson08.profile.ProfileService;
import com.example.lesson08.profile.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 投影片 A（JPQL / Native SQL / 命名查詢）與 B.2（一對一 User ↔ UserProfile） */
// com.example.controller / lesson07 都有 UserController，故指定專屬 bean 名稱
@RestController("lesson08UserController")
@RequestMapping("/lesson08/users")
public class UserController {

    private final ProfileService profileService;

    public UserController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public List<UserDto> list() {
        return profileService.list();
    }

    /** nativeSql = false 用 JPQL，true 用 Native SQL，比較 Console 印出的 SQL */
    @GetMapping("/status/{status}")
    public List<UserDto> byStatus(@PathVariable String status,
                                  @RequestParam(defaultValue = "false") boolean nativeSql) {
        return profileService.byStatus(status, nativeSql);
    }

    @GetMapping("/lookup")
    public UserDto lookup(@RequestParam String email, @RequestParam String lastName) {
        return profileService.lookup(email, lastName);
    }

    /** MySQL 專用語法：最近一天建立的使用者 */
    @GetMapping("/recent")
    public List<UserDto> recent() {
        return profileService.recent();
    }

    @GetMapping("/search")
    public List<UserDto> search(@RequestParam String keyword) {
        return profileService.search(keyword);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return profileService.get(id);
    }

    /** bio 有給值時，cascade 連同 UserProfile 一起新增 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserRequest request) {
        return profileService.create(request.username(), request.lastName(),
                request.email(), request.status(), request.bio());
    }

    @PutMapping("/{id}/profile")
    public UserDto updateBio(@PathVariable Long id, @RequestBody ProfileRequest request) {
        return profileService.updateBio(id, request.bio());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        profileService.delete(id);
    }

    public record UserRequest(String username, String lastName, String email, String status, String bio) { }

    public record ProfileRequest(String bio) { }
}
