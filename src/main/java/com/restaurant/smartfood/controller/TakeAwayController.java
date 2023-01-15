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
    public List<TakeAway> getTakeAwaysByDates(@PathVariable("startDate") String startDate,
                                               @PathVariable("endDate") String endDate) {
        return takeAwayService.getTakeAwaysByDates(startDate, endDate);
    }
    @GetMapping("/member/{memberid}")
    @AuthorizeRegisteredUser
    public List<TakeAway> getTakeAwaysByMember(@PathVariable("memberid") Long memberId) {
        return takeAwayService.getTakeAwaysByMember(memberId);
    }

    @GetMapping("/active")
    @AuthorizeEmployee
    public List<TakeAway> getActiveTakeAways() {
        return takeAwayService.getActiveTakeAways();
    }

    @GetMapping("/status/{status}")
    @AuthorizeEmployee
    public List<TakeAway> getTakeAwaysByStatus(@PathVariable("status") OrderStatus status) {
        return takeAwayService.getTakeAwaysByStatus(status);
    }
}
