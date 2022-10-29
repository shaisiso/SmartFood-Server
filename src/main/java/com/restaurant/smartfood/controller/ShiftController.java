package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.repostitory.ShiftRepository;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController()
@CrossOrigin
@RequestMapping("/api/shift")
public class ShiftController {

    @Autowired
    private ShiftRepository shiftRepository;

    @PostMapping
    public Shift addShift(@Valid @RequestBody Shift newShift) {
        return shiftRepository.save(newShift);
    }


}
