package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
//    @Query(value = "SELECT * FROM shift WHERE employee_phone_number =:id AND shift_entrance =:start_date",
//            nativeQuery = true)
//    Optional<Shift> findByEmployeePhoneNumberAndByShiftEntrance(@Param("id") String EmployeePhoneNumber,
//                                                       @Param("start_date") LocalDateTime startDate);

//    List<Shift> findByEmployeeIDAndByShiftEntranceIsBetween
//            (Long EmployeeID, LocalDateTime startDate, LocalDateTime endDate);
}