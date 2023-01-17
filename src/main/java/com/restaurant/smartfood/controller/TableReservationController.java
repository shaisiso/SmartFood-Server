package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.security.AuthorizeEmployee;
import com.restaurant.smartfood.security.AuthorizeManagers;
import com.restaurant.smartfood.security.AuthorizeRegisteredUser;
import com.restaurant.smartfood.service.TableReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController()
@CrossOrigin
@RequestMapping("/api/reservation")
public class TableReservationController {

    @Autowired
    private TableReservationService tableReservationService;

    @PostMapping
    public TableReservation addTableReservation(@Valid @RequestBody TableReservation reservation) {
        return tableReservationService.addTableReservation(reservation);
    }

    @PutMapping
    @AuthorizeRegisteredUser
    public TableReservation updateTableReservation(@Valid @RequestBody TableReservation updatedReservation) {
        return tableReservationService.updateTableReservation(updatedReservation);
    }

    @DeleteMapping("/{id}")
    @AuthorizeRegisteredUser
    public void deleteTableReservation(@PathVariable("id") Long reservationId) {
        tableReservationService.deleteTableReservation(reservationId);
    }

    @GetMapping
    @AuthorizeManagers
    public List<TableReservation> getAll(){
        return tableReservationService.findAll();
    }

    @GetMapping("/date/{startDate}/{endDate}")
    @AuthorizeEmployee
    public List<TableReservation> getTableReservationsByDates(@PathVariable("startDate") String startDate,
                                                              @PathVariable(required = false,
                                                                      name = "endDate") String endDate) {
        return tableReservationService.getTableReservationsByDates(startDate, endDate);
    }

    @GetMapping("/phoneNumber/{phoneNumber}")
    @AuthorizeRegisteredUser
    public List<TableReservation> getTableReservationsByCustomer(@PathVariable("phoneNumber") String phoneNumber) {
        return tableReservationService.getTableReservationsByCustomer(phoneNumber);
    }
    @GetMapping("/current")
    @AuthorizeEmployee
    public List<TableReservation> getCurrentReservations(){
        return tableReservationService.findCurrentReservations();
    }

    @GetMapping("/hours/{date}/{numberOfDiners}")
    public List<String> getAvailableHoursByDateAndDiners(@PathVariable("date") String dateSt, @PathVariable("numberOfDiners") Integer numberOfDiners){
        return tableReservationService.getAvailableHoursByDateAndDiners(dateSt,numberOfDiners);
    }
}
