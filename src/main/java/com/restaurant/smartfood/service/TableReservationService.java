package com.restaurant.smartfood.service;


import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.exception.UnprocessableEntityException;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.RestaurantTableRepository;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import com.restaurant.smartfood.utility.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@Transactional
@Slf4j
public class TableReservationService {
    private final TableReservationRepository tableReservationRepository;
    private final PersonService personService;
    private final RestaurantTableRepository restaurantTableRepository;
    private final MessageService messageService;
    private final WaitingListService waitingListService;
    @Value("${timezone.name}")
    private String timezone;
    @Value("${reservation-duration}")
    private int durationForReservation;

    @Autowired
    public TableReservationService(TableReservationRepository tableReservationRepository, PersonService personService, RestaurantTableRepository restaurantTableRepository, MessageService messageService, WaitingListService waitingListService) {
        this.tableReservationRepository = tableReservationRepository;
        this.personService = personService;
        this.restaurantTableRepository = restaurantTableRepository;
        this.messageService = messageService;
        this.waitingListService = waitingListService;
    }

    @Transactional
    public TableReservation addTableReservation(TableReservation reservation) {
        return addTableReservation(reservation, true);
    }

    @Transactional
    public TableReservation addTableReservation(TableReservation reservation, boolean sendMessage) {
        validateFutureDateTime(reservation);
        log.info(reservation.toString());
        String messageTitle = "New Reservation";
        List<RestaurantTable> freeTables = findSuitableTablesForReservation(reservation);
        if (freeTables.isEmpty())
            throw new ConflictException("There is no suitable table for your reservation request.");
        personService.savePerson(reservation.getPerson());
        reservation.setTable(freeTables.get(0));
        TableReservation savedReservation = tableReservationRepository.save(reservation);
        if (sendMessage)
            messageService.sendMessages(savedReservation.getPerson(), messageTitle, getNewReservationMsg(savedReservation));
        return savedReservation;
    }

    @Transactional
    public TableReservation updateTableReservation(TableReservation reservation) {
        validateFutureDateTime(reservation);
        boolean findTable = true;
        boolean oldTableFlag = false;
        boolean checkWaitingList = false;
        String messageTitle = "Reservation Update";
        TableReservation oldReservation = getTableReservationById(reservation.getReservationId());
        if (oldReservation.getDate().equals(reservation.getDate()) && oldReservation.getHour().equals(reservation.getHour())) {
            if (oldReservation.getNumberOfDiners().compareTo(reservation.getNumberOfDiners()) == 0) {
                findTable = false;
            } else if (oldReservation.getNumberOfDiners().compareTo(reservation.getNumberOfDiners()) > 0) {
                oldTableFlag = true; // look for smaller table but can stay also in the previous table
            }
            reservation.setTable(oldReservation.getTable());
        }
        reservation.setPerson(personService.getPersonByPhone(reservation.getPerson().getPhoneNumber()));
        if (findTable) {
            List<RestaurantTable> freeTables = findSuitableTablesForReservation(reservation);
            if (freeTables.isEmpty()) {
                if (!oldTableFlag)
                    throw new ConflictException("There is no suitable table for your reservation request.");
            } else {
                reservation.setTable(freeTables.get(0));
                checkWaitingList = true;  // the previous table was cleared, so maybe waiting list requests can be fulfilled
            }
        }
        personService.savePerson(reservation.getPerson());
        TableReservation savedReservation = tableReservationRepository.save(reservation);
        messageService.sendMessages(savedReservation.getPerson(), messageTitle, getNewReservationMsg(savedReservation));
        if (checkWaitingList)
            waitingListService.checkWaitingListsForTime(oldReservation.getDate(), oldReservation.getHour());
        return savedReservation;
    }

    private void validateFutureDateTime(TableReservation reservation) {
        LocalDateTime reservationDateTime = LocalDateTime.of(reservation.getDate(), reservation.getHour());
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of(timezone));
        if (reservationDateTime.compareTo(currentDateTime) <= 0)
            throw new UnprocessableEntityException("Reservation date and time should be in the future.");
    }

    private String getNewReservationMsg(TableReservation reservation) {
        return "Hi " + reservation.getPerson().getName() + ", Your reservation for " + reservation.getNumberOfDiners() + " diners at the date: "
                + reservation.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " at " + reservation.getHour() + ", was successfully saved ! Come Hungry !!";
    }

    public void deleteTableReservation(Long reservationId) {
        Optional<TableReservation> optionalTableReservation = tableReservationRepository.findById(reservationId);
        if (optionalTableReservation.isPresent()) {
            TableReservation reservation = optionalTableReservation.get();
            tableReservationRepository.delete(reservation);
            waitingListService.checkWaitingListsForTime(reservation.getDate(), reservation.getHour());
        } else {
            throw new ResourceNotFoundException("There is no reservation with ID: " + reservationId);
        }
    }

    public void deleteWaitingListReservation(TableReservation tableReservation) {
        if (tableReservationRepository.existsById(tableReservation.getReservationId()))
            tableReservationRepository.deleteById(tableReservation.getReservationId());
    }

    public TableReservation getTableReservationById(Long reservationId) {
        return tableReservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no reservation with ID: " + reservationId));
    }

    public List<TableReservation> getTableReservationsByDates(String startDateSt, String endDateSt) {
        try {
            LocalDate startDate = LocalDate.parse(startDateSt, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            if (endDateSt == null) {// table reservations for a single day
                return sortedReservation(tableReservationRepository.findByDate(startDate));
            }
            LocalDate endDate = LocalDate.parse(endDateSt, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return sortedReservation(tableReservationRepository.findByDateIsBetween(startDate, endDate));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BadRequestException("The request was in bad format");
        }

    }

    public List<TableReservation> getTableReservationsByCustomer(String phoneNumber) {
        return sortedReservation(tableReservationRepository.findByPersonPhoneNumber(phoneNumber));
    }

    private List<TableReservation> sortedReservation(List<TableReservation> reservationsList) {
        reservationsList.sort(TableReservation::compareTo);
        return reservationsList;
    }

    public List<TableReservation> findAll() {
        return tableReservationRepository.findAll();
    }

    private List<RestaurantTable> findSuitableTablesForReservation(TableReservation reservation) {
        // res = all table reservations in relevant date and hours
        LocalTime hourFrom = reservation.getHour().minusHours(durationForReservation);
        LocalTime hourTo = Utils.hourPlusDurationForReservation(reservation.getHour(), durationForReservation);
        if (hourTo.compareTo(reservation.getHour()) <= 0) //passed 00:00
            hourTo = LocalTime.of(23, 59);
        List<TableReservation> res = tableReservationRepository.
                findByDateAndHourGreaterThanAndHourLessThan(reservation.getDate(), hourFrom, hourTo);

        List<RestaurantTable> reservedTables = res.stream()
                .map(TableReservation::getTable)
                .collect(Collectors.toList());

        // freeTables = the free tables in the relevant hours
        List<RestaurantTable> freeTables = restaurantTableRepository.findByNumberOfSeatsGreaterThanEqual(reservation.getNumberOfDiners());
        freeTables.removeIf(reservedTables::contains);

        freeTables.sort(Comparator.comparing(RestaurantTable::getNumberOfSeats));
        return freeTables;

    }

    public List<TableReservation> findCurrentReservations() {
        // res = all table reservations for the next 2 hours
        return tableReservationRepository.
                findByDateIsAndHourIsBetween(LocalDate
                                .now(ZoneId.of(timezone)),
                        LocalTime.now(ZoneId.of(timezone)).minusMinutes(15),
                        Utils.hourPlusDurationForReservation(LocalTime.now(ZoneId.of(timezone)), durationForReservation));

    }


    public List<String> getAvailableHoursByDateAndDiners(String dateSt, Integer numberOfDiners) {
        try {
            LocalDate date = LocalDate.parse(dateSt, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            List<TableReservation> reservationsInDate = tableReservationRepository.findByDate(date);
            Set<RestaurantTable> reservedTables = reservationsInDate.stream()
                    .map(TableReservation::getTable)
                    .filter(t -> t.getNumberOfSeats() >= numberOfDiners)
                    .collect(Collectors.toSet());
            Map<RestaurantTable, Set<LocalTime>> tableReservedTimeMap = new HashMap<>();
            reservedTables.forEach(reservedTable -> tableReservedTimeMap.put(reservedTable, reservationsInDate.stream()
                    .filter(r -> r.getTable().getTableId().equals(reservedTable.getTableId()))
                    .map(TableReservation::getHour)
                    .collect(Collectors.toSet())));
            List<String> availableHours = new ArrayList<>();
            List<RestaurantTable> suitableTables = restaurantTableRepository.findByNumberOfSeatsGreaterThanEqual(numberOfDiners);


            for (int hour = 12; hour < 23; hour++) {
                for (int minute = 0; minute <= 30; minute += 30) {
                    LocalTime time = LocalTime.of(hour, minute);
                    for (RestaurantTable suitableTable : suitableTables) {
                        if (!tableReservedTimeMap.containsKey(suitableTable) || tableReservedTimeMap.get(suitableTable).stream()
                                .allMatch(reservedTime -> time.compareTo(Utils.hourPlusDurationForReservation(reservedTime, durationForReservation)) > 0
                                        || time.compareTo(reservedTime.minusHours(durationForReservation)) < 0)
                        ) {
                            availableHours.add(time.format(DateTimeFormatter.ofPattern("HH:mm")));
                            break;
                        }
                    }
                }
            }
            return availableHours;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getLocalizedMessage());
            throw new BadRequestException("The request was in a bad format");
        }
    }

}
