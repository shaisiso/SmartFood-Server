package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.security.AuthorizeEmployee;
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
    @AuthorizeEmployee
    public Shift enterShift(@Valid @RequestBody Shift newShift) {
        return shiftService.startShift(newShift);
    }

    @PutMapping
    @AuthorizeEmployee
    public Shift exitShift(@Valid @RequestBody Shift shift) {
        return shiftService.exitShift(shift);
    }

    @PutMapping("/update")
    @AuthorizeManagers
    public Shift updateShift(@Valid @RequestBody Shift shift) {
        return shiftService.updateShift(shift);
    }


    @DeleteMapping("/{shiftId}")
    @AuthorizeManagers
    public void deleteShift(@PathVariable("shiftId") Long shiftId) {
        shiftService.deleteShift(shiftId);
    }

    @GetMapping("/{phoneNumber}/{startDate}/{endDate}")
    @AuthorizeEmployee
    public List<Shift> getShiftsByEmployeeAndDates(
            @PathVariable("phoneNumber") String phoneNumber,
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate) {
        return shiftService.getShiftsByEmployeeAndDates(phoneNumber, startDate, endDate);
    }
    @GetMapping("/id/{id}/{startDate}/{endDate}")
    @AuthorizeEmployee
    public List<Shift> getShiftsByEmployeeIdAndDates(
            @PathVariable("id") Long employeeId,
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate) {
        return shiftService.getShiftsByEmployeeIdAndDates(employeeId, startDate, endDate);
    }

    @GetMapping("/{startDate}/{endDate}")
    @AuthorizeManagers
    public List<Shift> getShiftsByDates(
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate) {
        return shiftService.getShiftsByDates(startDate, endDate);
    }

    @GetMapping("/approve")
    @AuthorizeManagers
    public List<Shift> getAllShiftsToApprove() {
        return shiftService.getAllShiftsToApprove();
    }

    @GetMapping("/active/delivery")
    @AuthorizeManagers
    public List<Employee> findAllDeliveryGuyInShift() {
        return shiftService.findAllDeliveryGuyInShift();
    }
}
