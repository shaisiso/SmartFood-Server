package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.OrderOfTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderOfTableRepository extends JpaRepository<OrderOfTable, Long> {

    @Modifying
    @Query(value = "update orders_of_table set number_of_diners=:numberOfDiners, " +
            "table_table_id=:newTableId where id=:oldId", nativeQuery = true)
    void updateOrderOfTable(@Param("numberOfDiners") Integer numberOfDiners,
                        @Param("newTableId") Integer newTableId,
                        @Param("oldId") Long orderOfTableId);
}