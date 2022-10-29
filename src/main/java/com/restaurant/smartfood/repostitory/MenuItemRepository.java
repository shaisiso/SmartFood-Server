package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
//    @Query(value = "SELECT * FROM menu_items GROUP BY category"
//            , nativeQuery = true)
//    Object findAllGroupByCategory();
}