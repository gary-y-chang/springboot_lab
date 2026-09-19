package com.example.lesson08.hr;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    /** Lab 3：一次把部門與員工撈回，避免 N+1 */
    @Query("SELECT DISTINCT d FROM Department d JOIN FETCH d.employees")
    List<Department> findAllWithEmployees();

    /** Lab 3 另一解：宣告式 @EntityGraph */
    @EntityGraph(attributePaths = "employees")
    @Query("SELECT d FROM Department d")
    List<Department> findAllWithEmployeesGraph();
}
