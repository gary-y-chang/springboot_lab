package com.example.lesson07.controller;

import com.example.lesson07.dto.UserDto;
import com.example.lesson07.dto.UserRequest;
import com.example.lesson07.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User 版三層架構：Controller → Service → Repository → MySQL。
 *
 * <p>除了內建 CRUD，另外示範兩種查詢來源：
 * 衍生查詢（UserRepository 的 findByEmail / findByName / countByName）
 * 與自訂 Repository（UserCustomRepository 的動態查詢）。</p>
 */
// com.example.controller.UserController 同名，預設 bean 名稱都會是 userController，
// 會在啟動時拋 ConflictingBeanDefinitionException，故這裡指定專屬名稱。
@RestController("lesson07UserController")
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> list() {
        return service.findAll();
    }

    @GetMapping("/page")
    public List<UserDto> page(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        return service.findPage(page, size);
    }

    /** 自訂 Repository 的模糊查詢：name 或 email 任一包含 keyword。 */
    @GetMapping("/search")
    public List<UserDto> search(@RequestParam String keyword) {
        return service.search(keyword);
    }

    /** 衍生查詢：依 email 精準比對（路徑固定為 by-email，避免與 /{id} 衝突）。 */
    @GetMapping("/by-email")
    public UserDto byEmail(@RequestParam String email) {
        return service.findByEmail(email);
    }

    /** 衍生查詢：依 name 精準比對，可回傳多筆。 */
    @GetMapping("/by-name")
    public List<UserDto> byName(@RequestParam String name) {
        return service.findByName(name);
    }

    /** countByName：只取筆數，不撈整包資料。 */
    @GetMapping("/count")
    public long countByName(@RequestParam String name) {
        return service.countByName(name);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
