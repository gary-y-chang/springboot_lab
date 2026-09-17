package com.example.lesson07.controller;

import com.example.lesson07.dto.EmployeeDto;
import com.example.lesson07.dto.EmployeeRequest;
import com.example.lesson07.service.EmployeeService;
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
 * Lab 2：Controller → Service → Repository → H2，三層架構接上真實資料。
 */
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmployeeDto> list() {
        return service.findAll();
    }

    @GetMapping("/page")
    public List<EmployeeDto> page(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return service.findPage(page, size);
    }

    @GetMapping("/{id}")
    public EmployeeDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/name-email")
    public EmployeeDto get(@RequestParam String name, @RequestParam String email) { 
        return service.get(name, email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto create(@Valid @RequestBody EmployeeRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public EmployeeDto update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
