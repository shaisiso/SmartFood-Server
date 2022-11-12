package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.service.OrderService;
import com.restaurant.smartfood.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order addOrder(@Valid @RequestBody Order order) {
        return orderService.addOrder(order);
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
    }

    @PutMapping("/additem/{orderId}")
    public Order addItemToOrder(@PathVariable("orderId") Long orderId,
                                @Valid @RequestBody ItemInOrder item) {
        return orderService.addItemToOrder(orderId, item);
    }

    @PutMapping("/comment/{orderId}")
    public Order updateComment(@PathVariable("orderId") Long orderId,
                               @RequestBody String comment) {
        return orderService.updateComment(orderId, comment);
    }

    @PutMapping("/price/{orderId}/{amount}")
    public Order updateTotalPrice(@PathVariable("orderId") Long orderId,
                                  @PathVariable("amount") Float amount) {
        return orderService.updateTotalPrice(orderId, amount);
    }

    @PutMapping("/price/percent/{orderId}/{percent}")
    public Order applyDiscount(@PathVariable("orderId") Long orderId,
                               @PathVariable("percent") Integer percent) {
        return orderService.applyDiscount(orderId, percent);
    }

    @PutMapping("/price/member/{orderId}")
    public Order applyMemberDiscount(@PathVariable("orderId") Long orderId,
                                     @RequestBody Member member) {
        return orderService.applyMemberDiscount(orderId, member);
    }

    @PutMapping("/pay/{orderId}/{amount}")
    public Order payment(@PathVariable("orderId") Long orderId,
                         @PathVariable("amount") Float amount) {
        return orderService.payment(orderId, amount);
    }

    @PutMapping("/status/{orderId}/{status}")
    public Order updateStatus(@PathVariable("orderId") Long orderId,
                              @PathVariable("status") OrderStatus status) {
        return orderService.updateStatus(orderId, status);
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable("orderId") Long orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/date/{startDate}/{endDate}/{startTime}/{endTime}")
    public List<Order> getOrderByDates(@PathVariable("startDate") LocalDate startDate,
                                       @PathVariable("endDate") LocalDate endDate,
                                       @PathVariable(required = false, name = "startTime") LocalTime startTime,
                                       @PathVariable(required = false, name = "endTime") LocalTime endTime) {
        return orderService.getOrderByDates(startDate, endDate, startTime, endTime);
    }
}
