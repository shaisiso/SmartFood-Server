package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    //Optional<Employee> findByEmployeeID_Id(Long employeeID);
    Optional<Employee> findByPhoneNumber(String phoneNumber);


    @Modifying
    @Query(value = "insert into employees (id, password, role) VALUES (?1, ?2, ?3)", nativeQuery = true)
        void insertEmployee(Long id, String password,String role);

    @Modifying
    @Query(value = "update  employees set password=:password, role=:role where id=:id", nativeQuery = true)
    void updateEmployee(@Param("id") Long id,@Param("password") String password,@Param("role") String role);

}