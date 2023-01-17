package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.security.AuthorizeEmployee;
import com.restaurant.smartfood.security.AuthorizeManagers;
import com.restaurant.smartfood.service.OrderService;
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

    @PostMapping
    public Order addOrder(@Valid @RequestBody Order order) {
        return orderService.addOrder(order);
    }

    @DeleteMapping("/{orderId}")
    @AuthorizeManagers
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
    }

    @PostMapping("/item/{orderId}")
    public Order addItemToOrder(@PathVariable("orderId") Long orderId, @Valid @RequestBody ItemInOrder item) {
        return orderService.addItemToOrder(orderId, item);
    }

    @PostMapping("/item/list/{orderId}")
    public Order addItemsListToOrder(@PathVariable("orderId") Long orderId, @Valid @RequestBody List<ItemInOrder> items) {
        return orderService.addItemsListToOrder(orderId, items);
    }

    @PutMapping("/item")
    @AuthorizeManagers
    public Order updateItemInOrder(@RequestBody @Valid ItemInOrder item) {
        return orderService.updateItemInOrder(item);
    }

    @DeleteMapping("/item/{itemid}")
    @AuthorizeManagers
    public void deleteItemFromOrder(@PathVariable("itemid") Long itemId) {
        orderService.deleteItemFromOrder(itemId);
    }

    @PutMapping("/item/list")
    @AuthorizeManagers
    public Order deleteItemsListFromOrder(@RequestBody List<Long> itemsId) {
        return orderService.deleteItemsListFromOrder(itemsId);
    }

    @PutMapping("/comment/{orderId}")
    public Order updateComment(@PathVariable("orderId") Long orderId, @RequestBody String comment) {
        return orderService.updateComment(orderId, comment);
    }

    @PutMapping("/price/{orderId}/{amount}")
    @AuthorizeManagers
    public Order updateTotalPrice(@PathVariable("orderId") Long orderId, @PathVariable("amount") Float amount) {
        return orderService.updateTotalPrice(orderId, amount);
    }

    @PutMapping("/price/member/{orderId}/{phoneNumber}")
    public Order applyMemberDiscount(@PathVariable("orderId") Long orderId, @PathVariable("phoneNumber") String phoneNumber) {
        return orderService.applyMemberDiscount(orderId, phoneNumber);
    }

    @PutMapping("/pay/{orderId}/{amount}")
    @AuthorizeEmployee
    public Order payment(@PathVariable("orderId") Long orderId, @PathVariable("amount") Float amount) {
        return orderService.payment(orderId, amount);
    }

    @PutMapping("/status/{orderId}/{status}")
    @AuthorizeEmployee
    public Order updateStatus(@PathVariable("orderId") Long orderId, @PathVariable("status") OrderStatus status) {
        return orderService.updateStatus(orderId, status);
    }

    @PutMapping("/person/{orderId}")
    @AuthorizeEmployee
    public Order updatePerson(@PathVariable("orderId") Long orderId, @Valid @RequestBody Person person) {
        return orderService.updatePerson(orderId, person);
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable("orderId") Long orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping
    @AuthorizeManagers
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/datetime/{startDate}/{endDate}/{startTime}/{endTime}")
    public List<Order> getOrdersByDatesAndHours(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate, @PathVariable("startTime") String startTime, @PathVariable("endTime") String endTime) {
        return orderService.getOrdersByDatesAndHours(startDate, endDate, startTime, endTime);
    }

    @GetMapping("/date/{startDate}/{endDate}")
    public List<Order> getOrdersByDates(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        return orderService.getOrdersByDates(startDate, endDate);
    }


    @GetMapping("/statuses")
    public List<String> getAllStatuses() {
        return OrderStatus.getStatusNames();
    }
}