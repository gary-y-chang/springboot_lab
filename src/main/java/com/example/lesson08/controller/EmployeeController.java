package com.example.lesson08.controller;

import com.example.lesson08.hr.HrService;
import com.example.lesson08.hr.dto.EmployeeDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投影片 B.1：一對多的「擁有方」Employee（外鍵 dept_id）。
 * /lesson08/employees/search、/age、/by-dept 已在 HrController（Lab 2）。
 */
// com.example.lesson07.controller.EmployeeController 同名，故指定專屬 bean 名稱
@RestController("lesson08EmployeeController")
@RequestMapping("/lesson08/employees")
public class EmployeeController {

    private final HrService hrService;

    public EmployeeController(HrService hrService) {
        this.hrService = hrService;
    }

    /** 投影片 A.3：位置參數 ?1 ?2 的 JPQL */
    @GetMapping("/lookup")
    public EmployeeDto lookup(@RequestParam String email, @RequestParam String name) {
        return hrService.findByEmailAndName(email, name);
    }

    /** 投影片 A.4：Native SQL */
    @GetMapping("/older-than")
    public List<EmployeeDto> olderThan(@RequestParam int age) {
        return hrService.olderThanNative(age);
    }

    @GetMapping("/{id}")
    public EmployeeDto get(@PathVariable Long id) {
        return hrService.getEmployee(id);
    }

    /** 只更新有給值的欄位，靠 Dirty Checking 寫回 */
    @PutMapping("/{id}")
    public EmployeeDto update(@PathVariable Long id, @RequestBody EmployeeRequest request) {
        return hrService.updateEmployee(id, request.name(), request.age(), request.email());
    }

    /** 調部門：改擁有方的外鍵 dept_id */
    @PutMapping("/{id}/department/{deptId}")
    public EmployeeDto transfer(@PathVariable Long id, @PathVariable Long deptId) {
        return hrService.transfer(id, deptId);
    }

    public record EmployeeRequest(String name, Integer age, String email) { }
}
