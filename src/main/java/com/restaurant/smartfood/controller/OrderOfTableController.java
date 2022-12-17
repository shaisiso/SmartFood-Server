package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Discount;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.entities.OrderOfTable;
import com.restaurant.smartfood.entities.OrderStatus;
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
    public OrderOfTable addOrderOfTable(@RequestBody @Valid OrderOfTable orderOfTable) {
        return orderOfTableService.addOrderOfTable(orderOfTable);
    }

    @PutMapping
    public OrderOfTable updateOrderOfTable(@RequestBody @Valid OrderOfTable orderOfTable) {
        return orderOfTableService.updateOrderOfTable(orderOfTable);
    }
    @DeleteMapping("/{orderoftableid}")
    public void deleteOrderOfTable(@PathVariable("orderoftableid") Long id) {
        orderOfTableService.deleteOrderOfTable(id);
    }

    @GetMapping("/{orderid}")
    public OrderOfTable getOrderOfTableByOrderId(@PathVariable("orderid") Long orderId) {
        return orderOfTableService.getOrderOfTableByOrderId(orderId);
    }

    @GetMapping("/active")
    public List<OrderOfTable> getActiveOrdersOfTables() {
        return orderOfTableService.getActiveOrdersOfTables();
    }

    @GetMapping("/dates/{startDate}/{endDate}")
    public List<OrderOfTable> getOrdersOfTablesByDates(@PathVariable("startDate") String startDate,
                                                       @PathVariable("endDate") String endDate) {
        return orderOfTableService.getOrdersOfTablesByDates(startDate, endDate);
    }

    @GetMapping("/status/{status}")
    public List<OrderOfTable> getOrdersOfTablesByStatus(
            @PathVariable("status") OrderStatus status) {
        return orderOfTableService.getOrdersOfTablesByStatus(status);
    }
}
