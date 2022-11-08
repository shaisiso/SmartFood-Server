package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.service.OrderService;
import com.restaurant.smartfood.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders(){
        return orderService.getAllOrders();
    }

    @PostMapping
    public Order addOrder(@Valid @RequestBody Order order){
        return orderService.addOrder(order);
    }

    @PutMapping("/pay/{orderId}/{amount}")
    public Order updateOrder(@PathVariable("orderId") Long orderId,
                             @PathVariable("amount") Float amount) {
        return orderService.updatePayment(orderId, amount);

        //TODO continue order
    }
}
