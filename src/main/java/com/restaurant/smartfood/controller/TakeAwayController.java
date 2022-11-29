package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Delivery;
import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.entities.TakeAway;
import com.restaurant.smartfood.service.DeliveryService;
import com.restaurant.smartfood.service.TakeAwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/takeaway")
public class TakeAwayController {

    @Autowired
    private TakeAwayService takeAwayService;

    @PostMapping
    public TakeAway addTakeAway(@RequestBody @Valid TakeAway newTakeAway) {
        return takeAwayService.addTakeAway(newTakeAway);
    }

    @PutMapping
    public TakeAway updateTakeAway(@RequestBody @Valid TakeAway takeAway) {
        return takeAwayService.updateTakeAway(takeAway);
    }

    @DeleteMapping("/{orderId}")
    public void deleteTakeAway(@PathVariable("orderId") Long orderId) {
        takeAwayService.deleteTakeAway(orderId);
    }

    @GetMapping("/date/{startDate}/{endDate}")
    public List<TakeAway> getTakeAwaysByDates(@PathVariable("startDate") String startDate,
                                               @PathVariable("endDate") String endDate) {
        return takeAwayService.getTakeAwaysByDates(startDate, endDate);
    }
    @GetMapping("/member/{memberid}")
    public List<TakeAway> getTakeAwaysByMember(@PathVariable("memberid") Long memberId) {
        return takeAwayService.getTakeAwaysByMember(memberId);
    }

    @GetMapping("/active")
    public List<TakeAway> getActiveTakeAways() {
        return takeAwayService.getActiveTakeAways();
    }

    @GetMapping("/status/{status}")
    public List<TakeAway> getTakeAwaysByStatus(@PathVariable("status") OrderStatus status) {
        return takeAwayService.getTakeAwaysByStatus(status);
    }
}
