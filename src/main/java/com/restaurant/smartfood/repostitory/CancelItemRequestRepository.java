package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.CancelItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancelItemRequestRepository extends JpaRepository<CancelItemRequest, Long> {
    Optional<CancelItemRequest> findByItemInOrderId(Long itemInOrderId);
}