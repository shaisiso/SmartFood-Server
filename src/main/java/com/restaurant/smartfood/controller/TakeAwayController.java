package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.entities.TakeAway;
import com.restaurant.smartfood.security.AuthorizeEmployee;
import com.restaurant.smartfood.security.AuthorizeManagers;
import com.restaurant.smartfood.security.AuthorizeRegisteredUser;
import com.restaurant.smartfood.service.TakeAwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/takeaway")
public class TakeAwayController { //TODO shahar: check in post man

    @Autowired
    private TakeAwayService takeAwayService;

    @PostMapping
    public TakeAway addTakeAway(@RequestBody @Valid TakeAway newTakeAway) {
        return takeAwayService.addTakeAway(newTakeAway);
    }
    @DeleteMapping("/{orderId}")
    @AuthorizeManagers
    public void deleteTakeAway(@PathVariable("orderId") Long orderId) {
        takeAwayService.deleteTakeAway(orderId);
    }

    @GetMapping("/date/{startDate}/{endDate}")
    @AuthorizeEmployee
    public List<TakeAway> getTakeAwayListByDates(@PathVariable("startDate") String startDate,
                                                 @PathVariable("endDate") String endDate) {
        return takeAwayService.getTakeAwayListByDates(startDate, endDate);
    }
    @GetMapping("/member/{memberId}")
    @AuthorizeRegisteredUser
    public List<TakeAway> getTakeAwayListByMember(@PathVariable("memberId") Long memberId) {
        return takeAwayService.getTakeAwayListByMember(memberId);
    }

    @GetMapping("/active")
    @AuthorizeEmployee
    public List<TakeAway> getActiveTakeAwayList() {
        return takeAwayService.getActiveTakeAwayList();
    }

    @GetMapping("/status/{status}")
    @AuthorizeEmployee
    public List<TakeAway> getTakeAwayListByStatus(@PathVariable("status") OrderStatus status) {
        return takeAwayService.getTakeAwayListByStatus(status);
    }
}
