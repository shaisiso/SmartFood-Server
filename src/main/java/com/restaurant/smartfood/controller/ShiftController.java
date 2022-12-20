package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.security.AuthorizeManagers;
import com.restaurant.smartfood.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@CrossOrigin
@RequestMapping("/api/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping
    public Shift enterShift(@Valid @RequestBody Shift newShift) {
        return shiftService.startShift(newShift);
    }

    @PutMapping
    public Shift exitShift(@Valid @RequestBody Shift shift) {
        return shiftService.exitShift(shift);
    }

    @PutMapping("/update")
    @AuthorizeManagers
    public Shift updateShift(@Valid @RequestBody Shift shift) {
        return shiftService.updateShift(shift);
    }

    @AuthorizeManagers
    @DeleteMapping("/{shiftId}")
    public void deleteShift(@PathVariable("shiftId") Long shiftId) {
        shiftService.deleteShift(shiftId);
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
    @GetMapping("/approve")
    public List<Shift> getAllShiftsToApprove(){
        return shiftService.getAllShiftsToApprove();
    }
}
