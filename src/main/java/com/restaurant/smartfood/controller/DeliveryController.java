package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Delivery;
import com.restaurant.smartfood.entities.OrderOfTable;
import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping
    public Delivery addDelivery(@RequestBody @Valid Delivery newDelivery) {
        return deliveryService.addDelivery(newDelivery);
    }

    @PutMapping
    public Delivery updateDelivery(@RequestBody @Valid Delivery delivery) {
        return deliveryService.updateDelivery(delivery);
    }

    @DeleteMapping("/{orderId}")
    public void deleteDelivery(@PathVariable("orderId") Long orderId) {
        deliveryService.deleteDelivery(orderId);
    }

    @GetMapping("/date/{startDate}/{endDate}")
    public List<Delivery> getDeliveriesByDates(@PathVariable("startDate") String startDate,
                                               @PathVariable("endDate") String endDate) {
        return deliveryService.getDeliveriesByDates(startDate, endDate);
    }
    @GetMapping("/member/{memberid}")
    public List<Delivery> getDeliveriesByMember(@PathVariable("memberid") Long memberId) {
        return deliveryService.getDeliveriesByMember(memberId);
    }

    @GetMapping("/deliveryguy/{id}")
    public List<Delivery> getDeliveriesByDeliveryGuy(@PathVariable("id") Long id) {
        return deliveryService.getDeliveriesByDeliveryGuy(id);
    }

    @GetMapping("/status/{status}")
    public List<Delivery> getDeliveriesByStatus(@PathVariable("status") OrderStatus status) {
        return deliveryService.getDeliveriesByStatus(status);
    }

    @GetMapping("/active")
    public List<Delivery> getActiveDeliveries() {
        return deliveryService.getActiveDeliveries();
    }

}
