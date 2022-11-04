package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.repostitory.ShiftRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    public Shift saveShift(Shift newShift) {
        if (newShift.getShiftEntrance() == null)
            newShift.setShiftEntrance(LocalDateTime.now());
        return shiftRepository.save(newShift);
    }

    public Shift exitShift(Shift shift) {
        var shiftFound = shiftRepository.findById(shift.getShiftID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"The requested shift was not found"));

        shiftFound.setShiftExit(LocalDateTime.now());
        return shiftRepository.save(shiftFound);
    }

    public Shift updateShift(Shift shift) {
        shiftRepository.findById(shift.getShiftID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested shift was not found"));
        return shiftRepository.save(shift);
    }

    public void deleteShift(Shift shift) {
        shiftRepository.findById(shift.getShiftID()).ifPresentOrElse(s->{
                    shiftRepository.delete(shift);
                }, ()-> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The requested shift was not found");
        });
    }

    public List<Shift> getShiftsByEmployeeAndDates(String phoneNumber, String startDate, String endDate) {
        try{
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return shiftRepository.getShiftsByEmployeeAndDates(phoneNumber, start, end);
        }
        catch(Exception exception){
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The request was in bad format");
        }
    }

    public List<Shift> getShiftsByDates(String startDate, String endDate) {
        try{
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            return shiftRepository.findByShiftEntranceIsGreaterThanEqualAndShiftExitLessThanEqual
                    (start.atTime(0,0), end.atTime(0,0));
        }
        catch(Exception exception){
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The request was in bad format");
        }
    }
}
