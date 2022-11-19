package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.WaitingList;
import com.restaurant.smartfood.service.WaitingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/waitinglist")
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

        @DeleteMapping
        public void deleteFromWaitingList(@RequestBody @Valid WaitingList waitingList) {
                waitingListService.deleteFromWaitingList(waitingList);
        }

        @GetMapping("/date/{date}/{hour}")
        public List<WaitingList> getWaitingListByDateTime(@PathVariable("date") String date,
                                                          @PathVariable("hour") String hour) {
                return waitingListService.getWaitingListByDateTime(date, hour);
        }

        @GetMapping("/member/{memberid}")
        public List<WaitingList> getWaitingListByMember(@PathVariable("memberid") Long memberId) {
                return waitingListService.getWaitingListByMember(memberId);
        }
}
