package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Discount;
import com.restaurant.smartfood.entities.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<Discount> findByStartDateIsBetween(LocalDate startDate, LocalDate endDate);

//    @Query(value = "select * from discounts,discount_categories " +
//            "where discounts.discount_id=discount_categories.discount_discount_id and " +
//            "discount_categories.categories=:category", nativeQuery = true)
    List<Discount> findByCategories(ItemCategory category);

    List<Discount> findByStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual(
            LocalDate startDate, LocalDate endDate);
}