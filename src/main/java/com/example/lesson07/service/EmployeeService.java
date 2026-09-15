package com.example.lesson07.service;

import com.example.lesson07.dto.EmployeeDto;
import com.example.lesson07.dto.EmployeeRequest;
import com.example.lesson07.entity.Employee;
import com.example.lesson07.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Lab 2 參考解：Service 注入 Repository，直接呼叫內建方法。
 */
@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository repository;

    // 建構子注入（不需 @Autowired）
    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<EmployeeDto> findAll() {
        return repository.findAll().stream().map(EmployeeDto::from).toList();
    }

    public EmployeeDto get(Long id) {
        Employee employee = repository.findById(id)               // 回傳 Optional
                .orElseThrow(() -> new NoSuchElementException("Employee " + id + " not found"));
        return EmployeeDto.from(employee);
    }

    /** 分頁與排序：第 page 頁、每頁 size 筆，依 name 降冪。 */
    public List<EmployeeDto> findPage(int page, int size) {
        Page<Employee> result = repository.findAll(
                PageRequest.of(page, size, Sort.by("name").descending()));
        return result.getContent().stream().map(EmployeeDto::from).toList();
    }

    @Transactional
    public EmployeeDto create(EmployeeRequest request) {
        Employee saved = repository.save(                          // id 為 null → INSERT
                new Employee(request.name(), request.age(), request.email()));
        return EmployeeDto.from(saved);
    }

    @Transactional
    public EmployeeDto update(Long id, EmployeeRequest request) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee " + id + " not found"));
        employee.setName(request.name());
        employee.setAge(request.age());
        employee.setEmail(request.email());
        return EmployeeDto.from(repository.save(employee));        // id 已存在 → UPDATE
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Employee " + id + " not found");
        }
        repository.deleteById(id);
    }
}
