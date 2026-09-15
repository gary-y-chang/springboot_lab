package com.example.lesson07.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lesson07.dto.UserDto;
import com.example.lesson07.dto.UserRequest;
import com.example.lesson07.entity.User;
import com.example.lesson07.repository.UserCustomRepository;
import com.example.lesson07.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/** 投影片 C.2 內建 CRUD ＋ 分頁排序的完整可執行版本。 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;

    public UserService(UserRepository userRepository,
                       UserCustomRepository userCustomRepository) {
        this.userRepository = userRepository;
        this.userCustomRepository = userCustomRepository;
    }

    /** Create & Update：新增與修改共用 save()。 */
    public void createAndUpdate() {
        // 1. 新增使用者 (INSERT)
        User newUser = new User("Alice", "alice@example.com");
        User savedUser = userRepository.save(newUser);

        // 2. 修改使用者 (UPDATE)
        savedUser.setName("Alice Lin");
        userRepository.save(savedUser);          // id 已存在，自動轉為 update

        // 3. 批次儲存
        List<User> userList = Arrays.asList(
                new User("Bob", "bob@example.com"),
                new User("Cathy", "cathy@example.com"));
        userRepository.saveAll(userList);
    }

    /** Read：單筆、全選、存在檢查、計數。 */
    @Transactional(readOnly = true)
    public void read() {
        Optional<User> userOpt = userRepository.findById(1L);
        userOpt.ifPresent(user -> System.out.println(user.getName()));

        List<User> allUsers = userRepository.findAll();
        boolean exists = userRepository.existsById(1L);
        long count = userRepository.count();

        System.out.printf("all=%d exists=%s count=%d%n", allUsers.size(), exists, count);
    }

    /** Delete：ID、物件、批次、清空。 */
    public void delete(User userObj) {
        userRepository.deleteById(1L);
        userRepository.delete(userObj);
        userRepository.deleteAllById(Arrays.asList(1L, 2L, 3L));
        // userRepository.deleteAll();           // 清空整張表：危險，請小心使用
    }

    /** 分頁與排序。 */
    @Transactional(readOnly = true)
    public void pagination() {
        Page<User> userPage = userRepository.findAll(
                PageRequest.of(0, 10, Sort.by("name").descending()));

        List<User> content = userPage.getContent();        // 當頁資料清單
        long totalElements = userPage.getTotalElements();  // 資料庫總筆數
        int totalPages = userPage.getTotalPages();         // 總頁數

        System.out.printf("page=%d/%d total=%d%n", content.size(), totalPages, totalElements);
    }

    /** 標準與自訂 Repository 的混用。 */
    @Transactional(readOnly = true)
    public void processData() {
        User user = userRepository.findByEmail("test@example.com");
        List<User> complexList = userCustomRepository.findUsersByComplexCondition("Admin");
        System.out.printf("user=%s complex=%d%n", user, complexList.size());
    }

    // ---------------------------------------------------------------
    // 提供給 UserController 的 CRUD（回傳 DTO，不外洩 Entity）
    // ---------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        return UserDto.from(findEntity(id));
    }

    /** 分頁與排序：第 page 頁、每頁 size 筆，依 name 降冪。 */
    @Transactional(readOnly = true)
    public List<UserDto> findPage(int page, int size) {
        Page<User> result = userRepository.findAll(
                PageRequest.of(page, size, Sort.by("name").descending()));
        return result.getContent().stream().map(UserDto::from).toList();
    }

    /** 衍生查詢：依 email 精準比對，查無資料時 Repository 會回傳 null。 */
    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NoSuchElementException("User with email " + email + " not found");
        }
        return UserDto.from(user);
    }

    /** 衍生查詢：依 name 精準比對，可回傳多筆。 */
    @Transactional(readOnly = true)
    public List<UserDto> findByName(String name) {
        return userRepository.findByName(name).stream().map(UserDto::from).toList();
    }

    @Transactional(readOnly = true)
    public long countByName(String name) {
        return userRepository.countByName(name);
    }

    /** 自訂 Repository：name 或 email 任一包含 keyword 即命中。 */
    @Transactional(readOnly = true)
    public List<UserDto> search(String keyword) {
        return userCustomRepository.findUsersByComplexCondition(keyword)
                .stream().map(UserDto::from).toList();
    }

    public UserDto create(UserRequest request) {
        User saved = userRepository.save(                       // id 為 null → INSERT
                new User(request.name(), request.email()));
        return UserDto.from(saved);
    }

    public UserDto update(Long id, UserRequest request) {
        User user = findEntity(id);
        user.setName(request.name());
        user.setEmail(request.email());
        return UserDto.from(userRepository.save(user));         // id 已存在 → UPDATE
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    private User findEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User " + id + " not found"));
    }
}
