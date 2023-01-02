package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingList, Long> {

    Optional<WaitingList> findByPersonIdAndDateAndHour(Long memberId, LocalDate date, LocalTime time);

    List<WaitingList> findByDateAndHour(LocalDate localDate, LocalTime localHour);

    List<WaitingList> findByPersonId(Long memberId);
    List<WaitingList> findByDateIsAndHourIsBetween(LocalDate localDate, LocalTime startHour, LocalTime endHour);
}