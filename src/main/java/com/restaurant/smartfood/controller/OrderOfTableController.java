package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.security.AuthorizeEmployee;
import com.restaurant.smartfood.security.AuthorizeManagers;
import com.restaurant.smartfood.service.OrderOfTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/api/orderoftable")
public class OrderOfTableController {

    @Autowired
    private OrderOfTableService orderOfTableService;

    @PostMapping
    @AuthorizeEmployee
    public OrderOfTable addOrderOfTable(@RequestBody @Valid OrderOfTable orderOfTable) {
        return orderOfTableService.addOrderOfTable(orderOfTable);
    }

    @PutMapping
    @AuthorizeEmployee
    public OrderOfTable updateOrderOfTable(@RequestBody @Valid OrderOfTable orderOfTable) {
        return orderOfTableService.updateOrderOfTable(orderOfTable);
    }
    @DeleteMapping("/{orderoftableid}")
    @AuthorizeManagers
    public void deleteOrderOfTable(@PathVariable("orderoftableid") Long id) {
        orderOfTableService.deleteOrderOfTable(id);
    }

    @GetMapping("/{orderid}")
    @AuthorizeEmployee
    public OrderOfTable getOrderOfTableByOrderId(@PathVariable("orderid") Long orderId) {
        return orderOfTableService.getOrderOfTableByOrderId(orderId);
    }

    @GetMapping("/active")
    @AuthorizeEmployee
    public List<OrderOfTable> getActiveOrdersOfTables() {
        return orderOfTableService.getActiveOrdersOfTables();
    }
    @GetMapping("/active/{tableId}")
    @AuthorizeEmployee
    public OrderOfTable getActiveOrdersOfTable(@PathVariable("tableId") Integer tableId) {
        return orderOfTableService.getActiveOrdersOfTable(tableId);
    }
    @GetMapping("/dates/{startDate}/{endDate}")
    @AuthorizeEmployee
    public List<OrderOfTable> getOrdersOfTablesByDates(@PathVariable("startDate") String startDate,
                                                       @PathVariable("endDate") String endDate) {
        return orderOfTableService.getOrdersOfTablesByDates(startDate, endDate);
    }
    @GetMapping("/status/{status}")
    @AuthorizeEmployee
    public List<OrderOfTable> getOrdersOfTablesByStatus(
            @PathVariable("status") OrderStatus status) {
        return orderOfTableService.getOrdersOfTablesByStatus(status);
    }

    @PostMapping("/item/cancel")
    @AuthorizeEmployee
    public CancelItemRequest addRequestForCancelItem(@Valid @RequestBody CancelItemRequest cancelItemRequest){
        return  orderOfTableService.addRequestForCancelItem(cancelItemRequest);
    }
    @PutMapping("/item/cancel")
    @AuthorizeManagers
    public CancelItemRequest approveRequestForCancelItem(@Valid @RequestBody CancelItemRequest cancelItemRequest){
        return  orderOfTableService.approveRequestForCancelItem(cancelItemRequest);
    }
}
