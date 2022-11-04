package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.service.ShiftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController()
@CrossOrigin
@RequestMapping("/api/shift")
@Slf4j
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

    @PutMapping("/update")
    public Shift updateShift(@Valid @RequestBody Shift shift) {
        return shiftService.updateShift(shift);
    }

    @DeleteMapping
    public void deleteShift(@Valid @RequestBody Shift shift) {
        shiftService.deleteShift(shift);
    }

    @GetMapping("/{phoneNumber}/{startDate}/{endDate}")
    public List<Shift> getShiftsByEmployeeAndDates(
            @PathVariable("phoneNumber") String phoneNumber,
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate) {
        return shiftService.getShiftsByEmployeeAndDates(phoneNumber, startDate, endDate);
    }

    @GetMapping("/{startDate}/{endDate}")
    public List<Shift> getShiftsByDates(
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate) {
        return shiftService.getShiftsByDates(startDate, endDate);
    }
}
