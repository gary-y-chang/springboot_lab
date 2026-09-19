package com.example.lesson08.controller;

import com.example.lesson08.hr.HrService;
import com.example.lesson08.hr.dto.DepartmentDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投影片 B.1：一對多的「被動方」Department。
 * GET /lesson08/departments/{id} 與 /lesson08/departments/{id}/employees 已在 HrController（Lab 1）。
 */
@RestController
@RequestMapping("/lesson08/departments")
public class DepartmentController {

    private final HrService hrService;

    public DepartmentController(HrService hrService) {
        this.hrService = hrService;
    }

    /** fetch = lazy | join-fetch | entity-graph，比較 Console 印出的 SQL 筆數 */
    @GetMapping
    public List<DepartmentDto> list(@RequestParam(defaultValue = "lazy") String fetch) {
        return hrService.listDepartments(fetch);
    }

    @GetMapping("/by-name")
    public DepartmentDto byName(@RequestParam String name) {
        return hrService.getDepartmentByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDto create(@RequestBody DepartmentRequest request) {
        return hrService.createDepartment(request.name());
    }

    @PutMapping("/{id}")
    public DepartmentDto rename(@PathVariable Long id, @RequestBody DepartmentRequest request) {
        return hrService.renameDepartment(id, request.name());
    }

    /** cascade = ALL：部門與旗下員工一起刪除 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        hrService.deleteDepartment(id);
    }

    /** orphanRemoval = true：員工從部門集合移除後被自動刪除 */
    @DeleteMapping("/{id}/employees/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeEmployee(@PathVariable Long id, @PathVariable Long employeeId) {
        hrService.removeEmployee(id, employeeId);
    }

    public record DepartmentRequest(String name) { }
}
