package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.TableReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TableReservationRepository extends JpaRepository<TableReservation, Long>{
}