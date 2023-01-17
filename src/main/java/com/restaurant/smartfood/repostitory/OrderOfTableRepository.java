package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Delivery;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.entities.OrderOfTable;
import com.restaurant.smartfood.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderOfTableRepository extends JpaRepository<OrderOfTable, Long> {

    @Modifying
    @Query(value = "update orders_of_table set number_of_diners=:numberOfDiners, " +
            "table_table_id=:newTableId where id=:oldId", nativeQuery = true)
    void updateOrderOfTable(@Param("numberOfDiners") Integer numberOfDiners,
                        @Param("newTableId") Integer newTableId,
                        @Param("oldId") Long orderOfTableId);

    List<OrderOfTable> findByStatusIsNot(OrderStatus status);
    List<OrderOfTable> findByDateIsBetween(LocalDate startDate, LocalDate endDate);
    List<OrderOfTable> findByStatus(OrderStatus status);
}