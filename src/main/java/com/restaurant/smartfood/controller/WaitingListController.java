package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.entities.WaitingList;
import com.restaurant.smartfood.service.WaitingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/waiting-list")
public class WaitingListController {

    @Autowired
    private WaitingListService waitingListService;

    @PostMapping
    public WaitingList addToWaitingList(@RequestBody @Valid WaitingList waitingList) {
        return waitingListService.addToWaitingList(waitingList);
    }

    @PutMapping
    public WaitingList updateWaitingList(@RequestBody @Valid WaitingList waitingList) {
        return waitingListService.updateWaitingList(waitingList);
    }

    @DeleteMapping("/{id}")
    public void deleteFromWaitingList(@PathVariable("id") Long waitingListId) {
        waitingListService.deleteFromWaitingList(waitingListId);
    }

    @GetMapping("/date/{date}/{hour}")
    public List<WaitingList> getWaitingListByDateTime(@PathVariable("date") String date,
                                                      @PathVariable("hour") String hour) {
        return waitingListService.getWaitingListByDateTime(date, hour);
    }

    @GetMapping("/member/{memberId}")
    public List<WaitingList> getWaitingListByMember(@PathVariable("memberId") Long memberId) {
        return waitingListService.getWaitingListByMember(memberId);
    }

    @PostMapping("/approve/token")
    public TableReservation approveTokenForReservation(@RequestBody String token) {
        return waitingListService.approveReservation(token);
    }
}
