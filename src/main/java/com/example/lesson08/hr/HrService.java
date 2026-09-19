package com.example.lesson08.hr;

import com.example.exception.BusinessException;
import com.example.exception.ResourceNotFoundException;
import com.example.lesson08.hr.dto.DepartmentDto;
import com.example.lesson08.hr.dto.EmployeeDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class HrService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public HrService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    // ---------- Lab 1 ----------
    public DepartmentDto getDepartment(Long id) {
        Department d = departmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Department", id));
        return DepartmentDto.from(d);
    }

    public List<EmployeeDto> employeesOf(Long deptId) {
        return employeeRepository.findByDeptId(deptId).stream().map(EmployeeDto::from).toList();
    }

    @Transactional
    public EmployeeDto hire(Long deptId, String name, Integer age, String email) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> ResourceNotFoundException.of("Department", deptId));
        Employee employee = new Employee(name, age, email);
        dept.addEmployee(employee);                 // 雙向關聯兩端同時賦值
        // dept 已是 managed：flush 時 CascadeType.ALL 連帶寫入員工，並把自動產生的 id 回填到 employee。
        // 若改呼叫 departmentRepository.save(dept)，既有實體會走 merge，id 只會落在複本上，回傳的 id 變成 null。
        departmentRepository.flush();
        return EmployeeDto.from(employee);
    }

    // ---------- Lab 2 ----------
    public List<EmployeeDto> searchByName(String keyword) {
        return toDto(employeeRepository.findByNameContainingOrderByAgeDesc(keyword));
    }

    public List<EmployeeDto> searchByAge(int lo, int hi) {
        return toDto(employeeRepository.findByAgeBetweenOrderByAgeDesc(lo, hi));
    }

    public List<EmployeeDto> searchByDeptName(String deptName) {
        return toDto(employeeRepository.byDept(deptName));
    }

    // ---------- Lab 3 ----------
    /** 優化前：1 筆查員工 + N 筆查部門 */
    public List<EmployeeDto> listNPlusOne() {
        return toDto(employeeRepository.findAll());
    }

    /** 優化後：JOIN FETCH，只剩 1 筆 SQL */
    public List<EmployeeDto> listWithJoinFetch() {
        return toDto(employeeRepository.findAllWithDept());
    }

    /** 優化後：@EntityGraph，一樣只剩 1 筆 SQL */
    public List<EmployeeDto> listWithEntityGraph() {
        return toDto(employeeRepository.findAllBy());
    }

    // ---------- 部門：一對多集合端的 N+1 與 cascade / orphanRemoval ----------
    /**
     * fetch = lazy：1 筆查部門 + 每個部門 1 筆查員工（N+1）；
     * join-fetch / entity-graph：只剩 1 筆 SQL。
     */
    public List<DepartmentDto> listDepartments(String fetch) {
        List<Department> departments = switch (fetch) {
            case "join-fetch" -> departmentRepository.findAllWithEmployees();
            case "entity-graph" -> departmentRepository.findAllWithEmployeesGraph();
            default -> departmentRepository.findAll();
        };
        return departments.stream().map(DepartmentDto::from).toList();
    }

    public DepartmentDto getDepartmentByName(String name) {
        return departmentRepository.findByName(name)
                .map(DepartmentDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("找不到部門，name = " + name));
    }

    @Transactional
    public DepartmentDto createDepartment(String name) {
        return DepartmentDto.from(departmentRepository.save(new Department(name)));
    }

    /** Dirty Checking：交易結束時自動 UPDATE，不必呼叫 save() */
    @Transactional
    public DepartmentDto renameDepartment(Long id, String name) {
        Department dept = findDepartment(id);
        dept.setName(name);
        return DepartmentDto.from(dept);
    }

    /** cascade = ALL：刪部門時連帶刪除旗下所有員工（觀察 Console 的多筆 DELETE） */
    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepository.delete(findDepartment(id));
    }

    /** orphanRemoval = true：從集合移除的員工成為孤兒，會被自動 DELETE */
    @Transactional
    public void removeEmployee(Long deptId, Long employeeId) {
        Department dept = findDepartment(deptId);
        Employee employee = findEmployee(employeeId);
        if (employee.getDept() == null || !employee.getDept().getId().equals(deptId)) {
            throw new BusinessException("員工 " + employeeId + " 不屬於部門 " + deptId);
        }
        dept.removeEmployee(employee);
    }

    // ---------- 員工：擁有方（外鍵 dept_id）的操作 ----------
    public EmployeeDto getEmployee(Long id) {
        return EmployeeDto.from(findEmployee(id));
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, String name, Integer age, String email) {
        Employee employee = findEmployee(id);
        if (name != null) employee.setName(name);
        if (age != null) employee.setAge(age);
        if (email != null) employee.setEmail(email);
        return EmployeeDto.from(employee);
    }

    /**
     * 調部門：外鍵由擁有方 Employee 維護，改 employee.dept 就會 UPDATE dept_id。
     * 注意不要呼叫舊部門的 removeEmployee()：orphanRemoval 會把這位員工直接刪掉。
     */
    @Transactional
    public EmployeeDto transfer(Long employeeId, Long deptId) {
        Employee employee = findEmployee(employeeId);
        findDepartment(deptId).addEmployee(employee);
        return EmployeeDto.from(employee);
    }

    public EmployeeDto findByEmailAndName(String email, String name) {
        Employee employee = employeeRepository.findByEmailAndName(email, name);
        if (employee == null) {
            throw new ResourceNotFoundException("找不到員工，email = " + email + "，name = " + name);
        }
        return EmployeeDto.from(employee);
    }

    public List<EmployeeDto> olderThanNative(int age) {
        return toDto(employeeRepository.findOlderThanNative(age));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Department", id));
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Employee", id));
    }

    private List<EmployeeDto> toDto(List<Employee> employees) {
        return employees.stream().map(EmployeeDto::from).toList();
    }
}
