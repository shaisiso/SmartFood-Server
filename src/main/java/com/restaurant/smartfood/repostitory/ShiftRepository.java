package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    @Query(value = "SELECT * FROM shifts WHERE" +
            " employee_phone_number =:phoneNumber AND" +
            " shift_entrance >=:startDate AND" +
            " shift_exit <=:endDate", nativeQuery = true)
    List<Shift> getShiftsByEmployeeAndDates(@Param("phoneNumber") String phoneNumber,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    List<Shift> findByShiftEntranceIsGreaterThanEqualAndShiftExitLessThanEqual(LocalDateTime start, LocalDateTime end);
}