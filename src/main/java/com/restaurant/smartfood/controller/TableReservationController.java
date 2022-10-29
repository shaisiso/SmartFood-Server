package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.repostitory.PersonRepository;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController()
@CrossOrigin
@RequestMapping("/api/reservation")
@Slf4j
public class TableReservationController {

//    @Autowired
//    private TableReservationService tableReservationService;
@Autowired
private TableReservationRepository TableReservationRepository;
@Autowired
private PersonRepository personRepository;
    @PostMapping
    public TableReservation addTableReservation(@Valid @RequestBody TableReservation reservation){
        log.debug("Pass validation "+reservation.toString());
        if(reservation.getPerson().getEmail().isEmpty())
            reservation.getPerson().setEmail(null);

        personRepository.save(reservation.getPerson());
        return TableReservationRepository.save(reservation);
        // TODO: add real implementation
    }
}
