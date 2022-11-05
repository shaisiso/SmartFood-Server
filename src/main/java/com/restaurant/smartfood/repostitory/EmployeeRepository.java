package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByEmployeeID_Id(Long employeeID);
}