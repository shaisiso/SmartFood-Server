package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.EmployeeID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeIDRepository extends JpaRepository<EmployeeID, Long> {
}