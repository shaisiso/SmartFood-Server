package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.repostitory.ShiftRepository;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import com.restaurant.smartfood.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController()
@CrossOrigin
@RequestMapping("/api/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping
    public Shift enterShift(@Valid @RequestBody Shift newShift) {
        return shiftService.saveShift(newShift);
    }

    @PutMapping
    public Shift exitShift(@Valid @RequestBody Shift shift) {
        return shiftService.exitShift(shift);
    }
}
