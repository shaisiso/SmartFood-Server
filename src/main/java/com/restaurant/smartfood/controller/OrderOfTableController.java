package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.security.AuthorizeEmployee;
import com.restaurant.smartfood.security.AuthorizeManagers;
import com.restaurant.smartfood.service.OrderOfTableService;
import com.restaurant.smartfood.utility.ItemInOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    public OrderOfTable getOrderOfTableByOrderId(@PathVariable("orderid") Long orderId) {
        return orderOfTableService.getOrderOfTableByOrderId(orderId);
    }

    @GetMapping("/active")
    @AuthorizeEmployee
    public List<OrderOfTable> getActiveOrdersOfTables() {
        return orderOfTableService.getActiveOrdersOfTables();
    }

    @GetMapping("/active/{tableId}")
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

    @PostMapping("/cancel/item")
    public CancelItemRequest addRequestForCancelItem(@Valid @RequestBody CancelItemRequest cancelItemRequest) {
        return orderOfTableService.addRequestForCancelItem(cancelItemRequest);
    }
    @PostMapping("/cancel/item/delete")
    @AuthorizeManagers
    public CancelItemRequest addCancelItemRequestAndDeleteItem(@Valid @RequestBody CancelItemRequest cancelItemRequest){
        return orderOfTableService.addCancelItemRequestAndDeleteItem(cancelItemRequest);
    }
    /*
        approve or decline request
     */
    @PutMapping("/cancel/item")
    @AuthorizeManagers
    public void handleRequestForCancelItem(@Valid @RequestBody CancelItemRequest cancelItemRequest) {
         orderOfTableService.handleRequestForCancelItem(cancelItemRequest);
    }
    @GetMapping("/cancel/item/{tableId}")
    public List<ItemInOrderResponse> getItemsInOrderOfTableForCancel(@PathVariable("tableId") Integer tableId) {
        return orderOfTableService.getItemsInOrderOfTableForCancel(tableId);
    }
    @GetMapping("/cancel")
    @AuthorizeEmployee
    public List<CancelItemRequest> getAllCancelRequests() {
        return orderOfTableService.getAllCancelRequests();
    }

}
