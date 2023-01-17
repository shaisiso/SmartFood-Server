package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Discount;
import com.restaurant.smartfood.entities.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

   // List<Discount> findByStartDateIsBetween(LocalDate startDate, LocalDate endDate);

    List<Discount> findByStartDateIsBetweenAndStartHourIsLessThanEqualAndEndHourIsGreaterThanEqual
            (LocalDate startDate, LocalDate endDate, LocalTime hour1, LocalTime hour2);

    List<Discount> findByCategories(ItemCategory category);

    List<Discount> findByStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual(LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT * FROM discounts d " +
            "where d.start_date<=:endDate AND d.end_date>=:startDate AND d.start_hour<=:endHour AND d.end_hour>=:startHour"
    ,nativeQuery = true)
    List<Discount>  findByDatesAndHours(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("startHour") LocalTime startHour,
                                        @Param("endHour") LocalTime endHour);
}