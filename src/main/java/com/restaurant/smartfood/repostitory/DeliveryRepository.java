package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Delivery;
import com.restaurant.smartfood.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    @Modifying
    @Query(value = "update deliveries set delivery_guy_id=:deliveryGuyId where id=:id", nativeQuery = true)
    void updateDelivery(@Param("deliveryGuyId") Long deliveryGuyId, @Param("id") Long id);

    List<Delivery> findByDateIsBetween(LocalDate startDate, LocalDate endDate);
    List<Delivery> findByPersonId(Long id);
    List<Delivery> findByDeliveryGuyId(Long id);
    List<Delivery> findByStatus(OrderStatus status);
    List<Delivery> findByStatusIsNot(OrderStatus status);
}