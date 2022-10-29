package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
}