package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.EmployeeRole;
import com.restaurant.smartfood.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByEmployeePhoneNumberAndShiftEntranceIsBetween(String phoneNumber,LocalDateTime start, LocalDateTime end);

    List<Shift> findByShiftEntranceIsBetween(LocalDateTime start, LocalDateTime end);

    List<Shift> findByIsApproved(Boolean isApproved);

    List<Shift> findByShiftEntranceBetweenAndShiftExitIsNullAndEmployeeRoleIs(LocalDateTime entranceStart,
                                                                              LocalDateTime entranceEnd,
                                                                              EmployeeRole role);
}