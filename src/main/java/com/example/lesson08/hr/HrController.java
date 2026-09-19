package com.example.lesson08.hr;

import com.example.lesson08.hr.dto.DepartmentDto;
import com.example.lesson08.hr.dto.EmployeeDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lesson08")
public class HrController {

    private final HrService hrService;

    public HrController(HrService hrService) {
        this.hrService = hrService;
    }

    // ---------- Lab 1 ----------
    @GetMapping("/departments/{id}")
    public DepartmentDto department(@PathVariable Long id) {
        return hrService.getDepartment(id);
    }

    @GetMapping("/departments/{id}/employees")
    public List<EmployeeDto> employeesOfDepartment(@PathVariable Long id) {
        return hrService.employeesOf(id);
    }

    @PostMapping("/departments/{id}/employees")
    public EmployeeDto hire(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return hrService.hire(id,
                (String) body.get("name"),
                (Integer) body.get("age"),
                (String) body.get("email"));
    }

    // ---------- Lab 2 ----------
    @GetMapping("/employees/search")
    public List<EmployeeDto> searchByName(@RequestParam String keyword) {
        return hrService.searchByName(keyword);
    }

    @GetMapping("/employees/age")
    public List<EmployeeDto> searchByAge(@RequestParam int lo, @RequestParam int hi) {
        return hrService.searchByAge(lo, hi);
    }

    @GetMapping("/employees/by-dept")
    public List<EmployeeDto> searchByDept(@RequestParam String name) {
        return hrService.searchByDeptName(name);
    }

    // ---------- Lab 3：比較 Console 印出的 SQL 筆數 ----------
    @GetMapping("/lab3/n-plus-one")
    public List<EmployeeDto> nPlusOne() {
        return hrService.listNPlusOne();
    }

    @GetMapping("/lab3/join-fetch")
    public List<EmployeeDto> joinFetch() {
        return hrService.listWithJoinFetch();
    }

    @GetMapping("/lab3/entity-graph")
    public List<EmployeeDto> entityGraph() {
        return hrService.listWithEntityGraph();
    }
}
