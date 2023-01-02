package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.entities.WaitingList;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import com.restaurant.smartfood.repostitory.WaitingListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
@Slf4j
@Transactional
public class WaitingListService {

    @Autowired
    private WaitingListRepository waitingListRepository;
    @Autowired
    private TableReservationRepository tableReservationRepository;
    Timer timer = new Timer();
    boolean timePassed = false, customerResponse = false, tableTaken = false;

    public WaitingList addToWaitingList(WaitingList waitingList) {
        waitingListRepository.findByPersonIdAndDateAndHour(waitingList.getPerson().getId(),
                waitingList.getDate(), waitingList.getHour()).ifPresent(w ->
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "There is already waiting list request with those details.");
        });
        return waitingListRepository.save(waitingList);
    }

    public WaitingList updateWaitingList(WaitingList waitingList) {
        var w = waitingListRepository.findById(waitingList.getId()).orElseThrow(() ->
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no waiting list request with those details.");
        });
        if (waitingList.getDate().equals(w.getDate()) && waitingList.getHour().equals(w.getHour())) {
            w.setNumberOfDiners(waitingList.getNumberOfDiners());
            return waitingListRepository.save(w);
        }
        waitingListRepository.delete(w);
        return waitingListRepository.save(waitingList);
    }

    public void deleteFromWaitingList(WaitingList waitingList) {
        waitingListRepository.findById(waitingList.getId()).orElseThrow(() ->
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no waiting list request with those details.");
        });
        waitingListRepository.delete(waitingList);
    }

    public List<WaitingList> getWaitingListByDateTime(String date, String hour) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalTime localHour = LocalTime.parse(hour, DateTimeFormatter.ofPattern("HH:mm"));
            return waitingListRepository.findByDateAndHour(localDate, localHour);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public List<WaitingList> getWaitingListByMember(Long memberId) {
        return waitingListRepository.findByPersonId(memberId);
    }


    public void checkWaitingLists(LocalDate date, LocalTime hour, RestaurantTable table) {
        var waitingListCustomers =
                waitingListRepository.findByDateIsAndHourIsBetween
                        (date, hour.minusHours(1), hour.plusHours(1));
        if (waitingListCustomers.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no customer that should be notified");
        while (!tableTaken || !waitingListCustomers.isEmpty()) {
            var waitingList = waitingListCustomers.remove(0);
            //send SMS to waitingList.member.phoneNumber
            while (!timePassed) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        timePassed = true;
                    }
                }, 2 * 60 * 1000); // 2 minutes

                if (customerResponse) {
                    tableTaken = true;
                    TableReservation t = TableReservation.builder().
                            table(table)
                            .hour(hour)
                            .date(date)
                            .person(waitingList.getPerson())
                            .numberOfDiners(waitingList.getNumberOfDiners())
                            .build();
                    tableReservationRepository.saveAll(Arrays.asList(t));
                }
            }
        }
    }
}
