package com.restaurant.smartfood.service;


import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.repostitory.RestaurantTableRepository;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TableReservationService {

    @Autowired
    private TableReservationRepository tableReservationRepository;

    @Autowired
    private PersonService personService;
    @Autowired
    private WaitingListService waitingListService;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    public TableReservation saveTableReservation(TableReservation reservation) {
        var freeTables = findSuitableTable(reservation);
        if (freeTables.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is now suitable table for your reservation request.");

        personService.savePerson(reservation.getPerson());
        reservation.setTable(freeTables.get(0));
        return tableReservationRepository.save(reservation);
    }

    public void deleteTableReservation(TableReservation reservation) {
        tableReservationRepository.findById(reservation.getReservationId()).
                ifPresentOrElse((t) -> tableReservationRepository.delete(reservation),
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "There is no reservation with ID: " + reservation.getReservationId());
                        });

        //
        waitingListService.checkWaitingLists(reservation.getDate(), reservation.getHour(), reservation.getTable());

    }

    public List<TableReservation> getTableReservationsByDates(LocalDate startDate, LocalDate endDate) {
        if (endDate == null) {// table reservations for a single day
            return tableReservationRepository.findByDate(startDate);
        }
        return tableReservationRepository.findByDateIsBetween(startDate, endDate);
    }

    public List<TableReservation> getTableReservationsByCustomer(String phoneNumber) {
        return tableReservationRepository.findByPersonPhoneNumber(phoneNumber);
    }

    public List<TableReservation> findAll() {
        return tableReservationRepository.findAll();
    }

    private List<RestaurantTable> findSuitableTable(TableReservation reservation) {
        // res = all table reservations in relevant date and hours
        var res = tableReservationRepository.
                findByDateIsAndHourIsBetween(reservation.getDate(), reservation.getHour().minusHours(1),
                        reservation.getHour().plusHours(1));

        // remove irrelevant tables (size wise)
        res.removeIf(t -> res.contains(t.getNumberOfDiners() < reservation.getNumberOfDiners()));

        // busyTables = the busy tables from the reservations
        List<RestaurantTable> busyTables = res.stream()
                .map(TableReservation::getTable)
                .collect(Collectors.toList());

        // freeTables = the free tables in the relevant hours
        var freeTables = restaurantTableRepository.findAll();
        freeTables.removeIf(t -> busyTables.contains(t.getTableId()));

        freeTables.sort(Comparator.comparing(RestaurantTable::getNumberOfSeats));
        return freeTables;


    }

}
