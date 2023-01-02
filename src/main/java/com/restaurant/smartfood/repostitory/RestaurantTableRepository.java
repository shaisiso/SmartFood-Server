package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Integer> {

    List<RestaurantTable> findByNumberOfSeatsGreaterThanEqual(Integer numberOfSeats);
}