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

    Optional<WaitingList> findByMemberIdAndDateAndTime(Long memberId, LocalDate date, LocalTime time);

    List<WaitingList> findByDateAndTime(LocalDate localDate, LocalTime localHour);

    List<WaitingList> findByMemberId(Long memberId);
}