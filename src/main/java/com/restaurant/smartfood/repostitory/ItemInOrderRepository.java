package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.ItemInOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemInOrderRepository extends JpaRepository<ItemInOrder, Long> {

}