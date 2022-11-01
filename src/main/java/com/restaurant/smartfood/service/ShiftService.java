package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.repostitory.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    public Shift saveShift(Shift newShift) {
        if (newShift.getShiftEntrance() == null)
            newShift.setShiftEntrance(LocalDateTime.now());
        return shiftRepository.save(newShift);
    }

    public Shift exitShift(Shift shift) {
        if (shift.getShiftEntrance() == null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);

        var shiftFound = shiftRepository.findById(shift.getShiftID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        shiftFound.setShiftExit(LocalDateTime.now());
        return shiftRepository.save(shiftFound);
    }
}
