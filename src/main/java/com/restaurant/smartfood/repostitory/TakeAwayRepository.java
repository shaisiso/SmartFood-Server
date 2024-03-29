package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.entities.TakeAway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TakeAwayRepository extends JpaRepository<TakeAway, Long> {

    List<TakeAway> findByDateIsBetween(LocalDate localStartDate, LocalDate localEndDate);
    List<TakeAway> findByPersonId(Long memberId);
    List<TakeAway> findByStatusIsNot(OrderStatus closed);
    List<TakeAway> findByStatus(OrderStatus status);
}