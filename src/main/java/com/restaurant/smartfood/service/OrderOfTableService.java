package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.repostitory.CancelItemRequestRepository;
import com.restaurant.smartfood.repostitory.OrderOfTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderOfTableService {
    private final OrderOfTableRepository orderOfTableRepository;
    private final OrderService orderService;
    private final RestaurantTableService restaurantTableService;
    private final ItemInOrderService itemInOrderService;
    private final CancelItemRequestRepository cancelItemRequestRepository;
    @Value("${timezone.name}")
    private String timezone;

    public OrderOfTable addOrderOfTable(OrderOfTable orderOfTable) {
        optionalActiveTableOrder(orderOfTable.getTable().getTableId())
                .ifPresent(o -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Table number " + o.getTable().getTableId() + " has active order");
                });
        prepareOrderToSave(orderOfTable);
        return orderOfTableRepository.save(initOrderOfTable(orderOfTable));
    }

    private void prepareOrderToSave(OrderOfTable orderOfTable) {
        var o = (OrderOfTable) orderService.initOrder(orderOfTable);
        var orderInDB = orderOfTableRepository.save(o);
        orderInDB.getItems().forEach(i -> {
            i.setOrder(orderInDB);
            itemInOrderService.addItemToOrder(i);
        });
        var totalPrice = orderService.calculateTotalPrice(orderInDB);
        orderInDB.setOriginalTotalPrice(totalPrice);
        orderInDB.setTotalPriceToPay(totalPrice);
        orderOfTable.getTable().setIsBusy(true);
        restaurantTableService.updateRestaurantTable(orderOfTable.getTable());
    }

    public OrderOfTable updateOrderOfTable(OrderOfTable orderOfTable) {
        var originalOrder = orderOfTableRepository.findById(orderOfTable.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no order of table with the id: " + orderOfTable.getId())
        );
        if (!originalOrder.getTable().getTableId().equals(orderOfTable.getTable().getTableId()))
            checkTableAvailability(orderOfTable);
        orderOfTableRepository.updateOrderOfTable(orderOfTable.getNumberOfDiners(),
                orderOfTable.getTable().getTableId(), orderOfTable.getId());
        return orderOfTable;
    }

    public void deleteOrderOfTable(Long id) {
        var o = orderOfTableRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no order of table with the id " + id));
        orderOfTableRepository.delete(o);
    }

    public OrderOfTable getOrderOfTableByOrderId(Long orderId) {
        return orderOfTableRepository.findById(orderId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no order of table with the id: " + orderId)
        );
    }

    public List<OrderOfTable> getActiveOrdersOfTables() {
        return orderOfTableRepository.findByStatusIsNot(OrderStatus.CLOSED);
    }

    public List<OrderOfTable> getOrdersOfTablesByDates(String startDate, String endDate) {
        try {
            LocalDate localStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate localEndDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return orderOfTableRepository.findByDateIsBetween(localStartDate, localEndDate);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public List<OrderOfTable> getOrdersOfTablesByStatus(OrderStatus status) {
        return orderOfTableRepository.findByStatus(status);
    }

    private OrderOfTable initOrderOfTable(OrderOfTable orderOfTable) {
        orderOfTable.setDate(LocalDate.now(ZoneId.of(timezone)));
        orderOfTable.setHour(LocalTime.now(ZoneId.of(timezone)));
        orderOfTable.setStatus(OrderStatus.ACCEPTED);
        orderOfTable.setAlreadyPaid((float) 0);
        orderOfTable.setOriginalTotalPrice((float) 0);

        var orderInDB = orderOfTableRepository.save(orderOfTable);
        orderInDB.getItems().forEach(i -> {
            i.setOrder(orderInDB);
            itemInOrderService.addItemToOrder(i);
        });
        orderInDB.setOriginalTotalPrice(orderService.calculateTotalPrice(orderInDB));
        return orderInDB;
    }

    private RestaurantTable checkTableAvailability(OrderOfTable orderOfTable) {
        var table = restaurantTableService.getTable(
                orderOfTable.getTable().getTableId());
        if (table.getIsBusy()) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Table number " + table.getTableId() + " is busy");
        table.setIsBusy(true);
        return restaurantTableService.updateRestaurantTable(table);
    }

    public Optional<OrderOfTable> optionalActiveTableOrder(Integer tableId) {
        return orderOfTableRepository.findByStatusIsNot(OrderStatus.CLOSED)
                .stream()
                .filter(orderOfTable -> orderOfTable.getTable().getTableId().equals(tableId))
                .findAny();
    }

    public OrderOfTable getActiveOrdersOfTable(Integer tableId) {
        return optionalActiveTableOrder(tableId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no active order for this table"));
    }

    public CancelItemRequest addRequestForCancelItem(CancelItemRequest cancelItemRequest) {
        var itemInOrder = itemInOrderService.getItemInOrderById(cancelItemRequest.getItemInOrder().getId());
        cancelItemRequestRepository.findByItemInOrderId(itemInOrder.getId())
                .ifPresent(r-> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,"This item was already sent for cancel");
                });
        var fullRequest = CancelItemRequest.builder()
                .item(itemInOrder.getItem())
                .order(itemInOrder.getOrder())
                .date(LocalDateTime.now(ZoneId.of(timezone)))
                .itemInOrder(itemInOrder)
                .reason(cancelItemRequest.getReason())
                .build();
        return cancelItemRequestRepository.save(fullRequest);
    }

    public CancelItemRequest approveRequestForCancelItem(CancelItemRequest cancelItemRequest) {
        return  null;
    }
}
