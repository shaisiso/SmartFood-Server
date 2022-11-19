package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.WaitingList;
import com.restaurant.smartfood.repostitory.WaitingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class WaitingListService {

    @Autowired
    public WaitingListRepository waitingListRepository;

    public WaitingList addToWaitingList(WaitingList waitingList) {
        waitingListRepository.findByMemberIdAndDateAndTime(waitingList.getMember().getId(),
                waitingList.getDate(), waitingList.getTime()).ifPresent(w ->
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "There is already waiting list request with those details.");
        });
        return waitingListRepository.save(waitingList);
    }

    public WaitingList updateWaitingList(WaitingList waitingList) {
        var w = waitingListRepository.findById(waitingList.getId()).orElseThrow(()->
        {throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "There is no waiting list request with those details.");});
        if (waitingList.getDate().equals(w.getDate()) && waitingList.getTime().equals(w.getTime())) {
            w.setNumberOfDiners(waitingList.getNumberOfDiners());
            return waitingListRepository.save(w);
        }
        waitingListRepository.delete(w);
        return waitingListRepository.save(waitingList);
    }

    public void deleteFromWaitingList(WaitingList waitingList) {
        waitingListRepository.findById(waitingList.getId()).orElseThrow(()->
        {throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "There is no waiting list request with those details.");});
        waitingListRepository.delete(waitingList);
    }

    public List<WaitingList> getWaitingListByDateTime(String date, String hour) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalTime localHour = LocalTime.parse(hour, DateTimeFormatter.ofPattern("HH:mm"));
        return waitingListRepository.findByDateAndTime(localDate, localHour);
    }

    public List<WaitingList> getWaitingListByMember(Long memberId) {
        return waitingListRepository.findByMemberId(memberId);
    }
}
