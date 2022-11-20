package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.OrderOfTable;
import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.repostitory.OrderOfTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
public class OrderOfTableService {

    @Autowired
    private OrderOfTableRepository orderOfTableRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private ItemInOrderService itemInOrderService;

    @Value("${timezone.name}")
    private String timezone;

    public OrderOfTable addOrderOfTable(OrderOfTable orderOfTable) {
        var table = checkTableAvailability(orderOfTable);
        orderOfTable.setTable(table);
        return orderOfTableRepository.save(initOrderOfTable(orderOfTable));
    }
    public OrderOfTable updateOrderOfTable(OrderOfTable orderOfTable) {
        orderOfTableRepository.findById(orderOfTable.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no order of table with the id: " + orderOfTable.getId())
        );
        checkTableAvailability(orderOfTable);
        orderOfTableRepository.updateOrderOfTable(orderOfTable.getNumberOfDiners(),
                orderOfTable.getTable().getTableId(), orderOfTable.getId());
        return orderOfTable;
    }
    // TODO: cont orderOfTable
//
//    public void deleteOrderOfTable(OrderOfTable orderOfTable) {
//    }
//
//    public OrderOfTable getOrderOfTableByOrderId(Long orderId) {
//    }
//
//    public List<OrderOfTable> getActiveOrdersOfTables() {
//    }
//
//    public static List<OrderOfTable> getOrdersOfTablesByDates(String startDate, String endDate) {
//    }
//
//    public List<OrderOfTable> getOrdersOfTablesByStatus(OrderStatus status) {
//    }

    private OrderOfTable initOrderOfTable(OrderOfTable orderOfTable) {
        orderOfTable.setDate(LocalDate.now(ZoneId.of(timezone)));
        orderOfTable.setHour(LocalTime.now(ZoneId.of(timezone)));
        orderOfTable.setStatus(OrderStatus.ACCEPTED);
        orderOfTable.setAlreadyPaid((float) 0);
        orderOfTable.setTotalPrice((float) 0);

        var orderInDB = orderOfTableRepository.save(orderOfTable);
        orderInDB.getItems().forEach(i -> {
            i.setOrder(orderInDB);
            itemInOrderService.addItemToOrder(i);
        });
        orderInDB.setTotalPrice(orderService.calculateTotalPrice(orderInDB));
        return orderInDB;
    }
    private RestaurantTable checkTableAvailability(OrderOfTable orderOfTable)
    {
        var table = restaurantTableService.getTable(
                orderOfTable.getTable().getTableId());
        if (table.getIsBusy()) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Table number "+table.getTableId()+" is busy");
        table.setIsBusy(true);
        return restaurantTableService.updateRestaurantTable(table);
    }
}
