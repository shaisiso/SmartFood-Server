package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.TableReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TableReservationRepository extends JpaRepository<TableReservation, Long>{

    List<TableReservation> findByDate(LocalDate date);

    List<TableReservation> findByDateIsBetween(LocalDate startDate, LocalDate endDate);

    List<TableReservation> findByPersonPhoneNumber(String phoneNumber);

    List<TableReservation> findByDateIsAndHourIsBetween(LocalDate date, LocalTime startHour, LocalTime endHour);
}