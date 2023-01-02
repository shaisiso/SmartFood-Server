package com.restaurant.smartfood.service;


import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.repostitory.RestaurantTableRepository;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TableReservationService {

    @Autowired
    private TableReservationRepository tableReservationRepository;

    @Autowired
    private PersonService personService;
    @Autowired
    private WaitingListService waitingListService;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;
    @Value("${timezone.name}")
    private String timezone;
    @Value("${reservation-duration}")
    private int durationForReservation;

    public TableReservation saveTableReservation(TableReservation reservation) {
        var freeTables = findSuitableTablesForReservation(reservation);
        if (freeTables.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is now suitable table for your reservation request.");
        log.warn("duration: " + durationForReservation);
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

    public List<TableReservation> getTableReservationsByDates(String startDateSt, String endDateSt) {
        try {
            var startDate = LocalDate.parse(startDateSt, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            if (endDateSt == null) {// table reservations for a single day
                return sortedReservation(tableReservationRepository.findByDate(startDate));
            }
            var endDate = LocalDate.parse(endDateSt, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return sortedReservation(tableReservationRepository.findByDateIsBetween(startDate, endDate));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }

    }

    public List<TableReservation> getTableReservationsByCustomer(String phoneNumber) {
        return sortedReservation(tableReservationRepository.findByPersonPhoneNumber(phoneNumber));
    }
    private  List<TableReservation> sortedReservation(List<TableReservation> reservationsList){
        reservationsList.sort(TableReservation::compareTo);
        return reservationsList;
    }

    public List<TableReservation> findAll() {
        return tableReservationRepository.findAll();
    }

    private List<RestaurantTable> findSuitableTablesForReservation(TableReservation reservation) {
        // res = all table reservations in relevant date and hours
        var hourFrom = reservation.getHour().minusHours(durationForReservation);
        var hourTo = reservation.getHour().plusHours(durationForReservation);
        if (hourTo.compareTo(reservation.getHour()) != 1) //passed 00:00
            hourTo = LocalTime.of(23, 59);
        log.info("from :" + hourFrom + ". to: " + hourTo + ". date: " + reservation.getDate());
        var res = tableReservationRepository.
                findByDateIsAndHourIsBetween(reservation.getDate(), hourFrom, hourTo
                );
        var res2 = tableReservationRepository.findByDate(reservation.getDate());
        log.info(res.toString());
        log.info("res2" + res2.toString());
        List<RestaurantTable> reservedTables = res.stream()
                .map(TableReservation::getTable)
                .collect(Collectors.toList());
        log.debug(reservedTables.toString());

        // freeTables = the free tables in the relevant hours
        var freeTables = restaurantTableRepository.findByNumberOfSeatsGreaterThanEqual(reservation.getNumberOfDiners());
        freeTables.removeIf(t -> reservedTables.contains(t));

        freeTables.sort(Comparator.comparing(RestaurantTable::getNumberOfSeats));
        return freeTables;

    }

    public List<TableReservation> findCurrentReservations() {
        // res = all table reservations for the next 2 hours
        return tableReservationRepository.
                findByDateIsAndHourIsBetween(LocalDate
                                .now(ZoneId.of(timezone)),
                        LocalTime.now(ZoneId.of(timezone)).minusMinutes(15),
                        LocalTime.now(ZoneId.of(timezone)).plusHours(durationForReservation));
    }

}
