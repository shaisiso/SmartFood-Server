package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.CancelItemRequestRepository;
import com.restaurant.smartfood.repostitory.OrderOfTableRepository;
import com.restaurant.smartfood.utility.ItemInOrderResponse;
import com.restaurant.smartfood.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrderOfTableService {
    private final OrderOfTableRepository orderOfTableRepository;
    private final OrderService orderService;
    private final RestaurantTableService restaurantTableService;
    private final TableReservationService tableReservationService;
    private final ItemInOrderService itemInOrderService;
    private final CancelItemRequestRepository cancelItemRequestRepository;
    private final WebSocketService webSocketService;
    private final MessageService messageService;
    @Value("${timezone.name}")
    private String timezone;

    @Autowired
    public OrderOfTableService(OrderOfTableRepository orderOfTableRepository, @Lazy OrderService orderService,
                               RestaurantTableService restaurantTableService, TableReservationService tableReservationService,
                               ItemInOrderService itemInOrderService, CancelItemRequestRepository cancelItemRequestRepository,
                               WebSocketService webSocketService, MessageService messageService) {
        this.orderOfTableRepository = orderOfTableRepository;
        this.orderService = orderService;
        this.restaurantTableService = restaurantTableService;
        this.tableReservationService = tableReservationService;
        this.itemInOrderService = itemInOrderService;
        this.cancelItemRequestRepository = cancelItemRequestRepository;
        this.webSocketService = webSocketService;
        this.messageService = messageService;
    }


    public OrderOfTable addOrderOfTable(OrderOfTable orderOfTable) {
        optionalActiveTableOrder(orderOfTable.getTable().getTableId())
                .ifPresent(o -> {
                    throw new ConflictException("Table number " + o.getTable().getTableId() + " has active order");
                });
        return orderOfTableRepository.save(prepareOrderToSave(orderOfTable));
    }

    private OrderOfTable prepareOrderToSave(OrderOfTable orderOfTable) {
        orderOfTable = (OrderOfTable) orderService.initOrder(orderOfTable);
        OrderOfTable orderInDB = orderOfTableRepository.save(orderOfTable);
        orderInDB.getItems().forEach(i -> {
            i.setOrder(orderInDB);
            itemInOrderService.addItemToOrder(i);
        });

        orderService.calculateTotalPrices(orderInDB);
        orderInDB.getTable().setIsBusy(true);
        restaurantTableService.updateRestaurantTable(orderInDB.getTable());
        return orderInDB;
    }

    public OrderOfTable updateOrderOfTable(OrderOfTable orderOfTable) {
        OrderOfTable originalOrder = orderOfTableRepository.findById(orderOfTable.getId()).orElseThrow(() ->
                new ResourceNotFoundException("There is no order of table with the id: " + orderOfTable.getId())
        );
        if (!originalOrder.getTable().getTableId().equals(orderOfTable.getTable().getTableId())) {
            checkTableIsNotBusy(orderOfTable);
            // TODO: Test New Change
            List<RestaurantTable> reservedTables = tableReservationService.findCurrentReservations()
                    .stream()
                    .map(TableReservation::getTable)
                    .collect(Collectors.toList());
            if (reservedTables.contains(orderOfTable.getTable()))
                throw new ConflictException( "Table number " + orderOfTable.getTable().getTableId() + " is reserved.");
        }

        orderOfTableRepository.updateOrderOfTable(orderOfTable.getNumberOfDiners(),
                orderOfTable.getTable().getTableId(), orderOfTable.getId());
        return orderOfTable;
    }

    public void deleteOrderOfTable(Long id) {
        OrderOfTable o = orderOfTableRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("There is no order of table with the id " + id));
        List<CancelItemRequest> cancelRequests = cancelItemRequestRepository.findByOrderOfTableId(o.getId());
        for (CancelItemRequest cancelRequest : cancelRequests) {
            cancelRequest.setOrderOfTable(null);
        }
        cancelItemRequestRepository.saveAll(cancelRequests);
        orderOfTableRepository.delete(o);
    }

    public OrderOfTable getOrderOfTableByOrderId(Long orderId) {
        return orderOfTableRepository.findById(orderId).orElseThrow(() ->
                new ResourceNotFoundException( "There is no order of table with the id: " + orderId));
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
            throw new BadRequestException( "The request was in bad format");
        }
    }

    public List<OrderOfTable> getOrdersOfTablesByStatus(OrderStatus status) {
        return orderOfTableRepository.findByStatus(status);
    }


    private void checkTableIsNotBusy(OrderOfTable orderOfTable) {
        RestaurantTable table = restaurantTableService.getTableById(orderOfTable.getTable().getTableId());
        if (table.getIsBusy())
            throw new ConflictException( "Table number " + table.getTableId() + " is busy");
        table.setIsBusy(true);
        restaurantTableService.updateRestaurantTable(table);
    }

    public Optional<OrderOfTable> optionalActiveTableOrder(Integer tableId) {
        return orderOfTableRepository.findByStatusIsNot(OrderStatus.CLOSED)
                .stream()
                .filter(orderOfTable -> orderOfTable.getTable().getTableId().equals(tableId))
                .findAny();
    }

    public OrderOfTable getActiveOrdersOfTable(Integer tableId) {
        return optionalActiveTableOrder(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no active order for this table"));
    }

    public CancelItemRequest addRequestForCancelItem(CancelItemRequest cancelItemRequest) {
        CancelItemRequest fullRequest = buildFullRequest(cancelItemRequest, false);
        webSocketService.notifyCancelItemRequest(fullRequest);
        return cancelItemRequestRepository.save(fullRequest);
    }

    public CancelItemRequest addCancelItemRequestAndDeleteItem(CancelItemRequest cancelItemRequest) {
        CancelItemRequest fullRequest = buildFullRequest(cancelItemRequest, true);
        orderService.deleteItemFromOrder(fullRequest.getItemInOrder().getId());
        fullRequest.setItemInOrder(null);
        return cancelItemRequestRepository.save(fullRequest);
    }

    private CancelItemRequest buildFullRequest(CancelItemRequest cancelItemRequest, Boolean isApproved) {
        ItemInOrder itemInOrder = itemInOrderService.getItemInOrderById(cancelItemRequest.getItemInOrder().getId());
        cancelItemRequestRepository.findByItemInOrderIdAndIsApprovedIsFalse(itemInOrder.getId())
                .ifPresent(r -> {
                    throw new ConflictException( "This item was already sent for cancel");
                });
        OrderOfTable orderOfTable = getOrderOfTableByOrderId(itemInOrder.getOrder().getId());
        return CancelItemRequest.builder()
                .menuItem(itemInOrder.getItem())
                .orderOfTable(orderOfTable)
                .date(LocalDateTime.now(ZoneId.of(timezone)))
                .itemInOrder(itemInOrder)
                .reason(cancelItemRequest.getReason())
                .isApproved(isApproved)
                .build();
    }

    public void handleRequestForCancelItem(CancelItemRequest cancelItemRequest) {
        CancelItemRequest cancelRequestInDB = getCancelItemRequestById(cancelItemRequest.getId());
        cancelRequestInDB.setIsApproved(cancelItemRequest.getIsApproved());
        if (cancelItemRequest.getIsApproved()) {
            ItemInOrder itemInOrder = cancelRequestInDB.getItemInOrder();
            cancelRequestInDB.setItemInOrder(null);
            orderService.deleteItemFromOrder(itemInOrder.getId());
            cancelItemRequestRepository.save(cancelRequestInDB);
        } else {
            cancelItemRequestRepository.delete(cancelRequestInDB);
        }
        webSocketService.notifyCancelItemRequest(cancelRequestInDB);
    }

    public CancelItemRequest getCancelItemRequestById(Long id) {
        return cancelItemRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException( "There is no request with id: " + id));
    }

    public List<ItemInOrderResponse> getItemsInOrderOfTableForCancel(Integer tableId) {
        RestaurantTable table = restaurantTableService.getTableById(tableId);
        if (!table.getIsBusy())
            throw new BadRequestException("The requested table is not busy");
        OrderOfTable orderOfTable = getActiveOrdersOfTable(tableId);
        return cancelItemRequestRepository.findByOrderOfTableIdAndIsApprovedIsFalse(orderOfTable.getId())
                .stream()
                .map(cr -> ItemInOrderResponse.buildItemInOrderResponse(cr.getItemInOrder()))
                .collect(Collectors.toList());
    }

    public List<CancelItemRequest> getAllCancelRequests() {
        return cancelItemRequestRepository.findByIsApprovedIsFalse();
    }

    // return true - is order of table, false - otherwise
    public void closeIfOrderOfTable(Order order) {
        orderOfTableRepository.findById(order.getId())
                .ifPresent(oot -> {
                    order.setStatus(OrderStatus.CLOSED);
                    messageService.sendMessages(order.getPerson(), "Your Order", "Thank you for choosing Smart Food ! We hope you enjoyed your meal.");
                    restaurantTableService.changeTableBusy(oot.getTable().getTableId(), false);
                });
    }
}