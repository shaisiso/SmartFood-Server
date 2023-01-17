package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDateIsBetweenAndHourIsBetween(LocalDate startDate, LocalDate endDate,
                                                    LocalTime startTime, LocalTime endTime);
}