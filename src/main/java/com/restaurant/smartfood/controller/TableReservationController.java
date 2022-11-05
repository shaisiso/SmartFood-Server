package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Customer;
import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.repostitory.PersonRepository;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import com.restaurant.smartfood.service.TableReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController()
@CrossOrigin
@RequestMapping("/api/reservation")
@Slf4j
public class TableReservationController {

    @Autowired
    private TableReservationService tableReservationService;

    @PostMapping
    public TableReservation addTableReservation(@Valid @RequestBody TableReservation reservation) {
        return tableReservationService.saveTableReservation(reservation);
    }

    @PutMapping
    public TableReservation updateTableReservation(@Valid @RequestBody TableReservation updatedReservation) {
        return tableReservationService.saveTableReservation(updatedReservation);
    }

    @DeleteMapping
    public void deleteTableReservation(@RequestBody TableReservation reservation) {
        tableReservationService.deleteTableReservation(reservation);
    }

    @GetMapping
    public List<TableReservation> getAll(){
        return tableReservationService.findAll();
    }

    @GetMapping("/date/{startDate}/{endDate}")
    public List<TableReservation> getTableReservationsByDates(@PathVariable("startDate") LocalDate startDate,
                                                              @PathVariable(required = false,
                                                                      name = "endDate") LocalDate endDate) {
        return tableReservationService.getTableReservationsByDates(startDate, endDate);
    }

    @GetMapping("/phoneNumber/{phoneNumber}")
    public List<TableReservation> getTableReservationsByCustomer(@PathVariable("phoneNumber") String phoneNumber) {
        return tableReservationService.getTableReservationsByCustomer(phoneNumber);
    }
}
