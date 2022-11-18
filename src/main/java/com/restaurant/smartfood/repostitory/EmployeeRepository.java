package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeID;
import com.restaurant.smartfood.entities.EmployeeRole;
import com.restaurant.smartfood.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    //Optional<Employee> findByEmployeeID_Id(Long employeeID);
    Optional<Employee> findByPhoneNumber(String phoneNumber);

    @Query(nativeQuery = true,value = "INSERT INTO employees (password,role , id) V VALUES (:pass,:role,:id)")
    void saveEmployee(@Param("pass") String password , @Param("role") EmployeeRole role, @Param("id") Long id);
}