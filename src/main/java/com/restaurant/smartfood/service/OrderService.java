package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.repostitory.MemberRepository;
import com.restaurant.smartfood.repostitory.OrderRepository;
import com.restaurant.smartfood.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemInOrderService itemInOrderService;
    private final MemberService memberService;
    private final DiscountService discountService;
    private final WebSocketService webSocketService;
    private final OrderOfTableService orderOfTableService;
    @Value("${timezone.name}")
    private String timezone;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order initOrder(Order order) {
        order.setDate(LocalDate.now(ZoneId.of(timezone)));
        order.setHour(LocalTime.now(ZoneId.of(timezone)));
        order.setStatus(OrderStatus.ACCEPTED);
        order.setAlreadyPaid((float) 0);
        order.setOriginalTotalPrice((float) 0);
        order.setTotalPriceToPay((float) 0.0);
        return order;
    }

    public Order addOrder(Order order) {
        var o = initOrder(order);
        var orderInDB = orderRepository.save(o);
        orderInDB.getItems().forEach(i -> {
            i.setOrder(orderInDB);
            itemInOrderService.addItemToOrder(i);
        });
//        var totalPrice = calculateTotalPrice(orderInDB);
//        orderInDB.setOriginalTotalPrice(totalPrice);
//        orderInDB.setTotalPriceToPay(totalPrice);
        calculateTotalPrices(orderInDB);
        return orderRepository.save(orderInDB);
    }

    public Order addItemToOrder(Long orderId, ItemInOrder item) {
        var order = getOrder(orderId);
        item.setOrder(order);
        var itemInOrder = itemInOrderService.addItemToOrder(item);
        order.getItems().add(itemInOrder);
        // order.setOriginalTotalPrice(calculateTotalPrice(order));
        calculateTotalPrices(order);
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public Order deleteItemsListFromOrder(List<Long> itemsInOrderId) {
        if (itemsInOrderId == null || itemsInOrderId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "List of items is missing");
        var order = itemInOrderService.getItemInOrderById(itemsInOrderId.get(0)).getOrder();
        itemInOrderService.deleteItemsListFromOrder(itemsInOrderId);
        calculateTotalPrices(order);
        webSocketService.notifyExternalOrders(order);
        return order;
    }

    public Order addItemsListToOrder(Long orderId, List<ItemInOrder> items) {
        var order = getOrder(orderId);
        var itemsInOrder = itemInOrderService.addListOfItemsToOrder(items, order);
        order.getItems().addAll(itemsInOrder);
        //  order.setOriginalTotalPrice(calculateTotalPrice(order));
        calculateTotalPrices(order);
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public void calculateTotalPrices(Order order) {
        checkIfEntitledForDiscount(order);
        var priceToPay = order.getItems()
                .stream()
                .map(i -> i.getPrice())
                .reduce((float) 0, Float::sum);
        var originalTotalPrice =
                order.getItems()
                        .stream()
                        .map(i -> i.getItem().getPrice())
                        .reduce((float) 0, Float::sum);
        order.setTotalPriceToPay(priceToPay);
        order.setOriginalTotalPrice(originalTotalPrice);
    }

    public void calculateTotalPricesForMembers(Order order) {
        membersCheckIfEntitledForDiscount(order);
        var priceToPay = order.getItems()
                .stream()
                .map(i -> i.getPrice())
                .reduce((float) 0, Float::sum);
        order.setTotalPriceToPay(priceToPay);
    }

    public Order payment(Long orderId, Float amount) {
        var order = getOrder(orderId);
        amount = normalizeAmount(amount,order);
        if (order.getTotalPriceToPay() < amount + order.getAlreadyPaid())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't pay more then the remaining amount.");
        order.setAlreadyPaid(order.getAlreadyPaid() + amount);
        if (order.getAlreadyPaid().equals(order.getTotalPriceToPay())) {
            orderOfTableService.closeIfOrderOfTable(order);
        }
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }
    private Float normalizeAmount(Float amountReceived ,Order order){
        var remainingAmount = order.getTotalPriceToPay() - order.getAlreadyPaid();
        if (Math.abs(amountReceived-remainingAmount) <0.1){
            return  remainingAmount;
        }
        return amountReceived;
    }
    public Order updateComment(Long orderId, String comment) {
        var order = getOrder(orderId);
        order.setOrderComment(comment);
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public Order updateTotalPrice(Long orderId, Float amount) {
        var order = getOrder(orderId);
        if (amount > order.getOriginalTotalPrice())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The amount is bigger than the total price");
        order.setTotalPriceToPay(order.getOriginalTotalPrice() - amount);
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    // TODO: check member discount from DiscountService
    public Order applyMemberDiscount(Long orderId, String phoneNumber) {
        var order = getOrder(orderId);
        memberService.getMemberByPhoneNumber(phoneNumber);
        calculateTotalPricesForMembers(order);
        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no order with the id: " + orderId));
    }

    public List<Order> getOrdersByDatesAndHours(String startDateStr, String endDateStr, String startTimeStr, String endTimeStr) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            return orderRepository.findByDateIsBetweenAndHourIsBetween(startDate, endDate, startTime, endTime);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public Order updateStatus(Long orderId, OrderStatus status) {
        var order = getOrder(orderId);
        order.setStatus(status);
        order = orderRepository.save(order);
        webSocketService.notifyExternalOrders(order);
        return order;
    }

    public void deleteOrder(Long orderId) {
        var order = getOrder(orderId);
        orderRepository.delete(order);
        webSocketService.notifyExternalOrders(order);
    }

    public List<Order> getOrdersByDates(String startDate, String endDate) {
        return getOrdersByDatesAndHours(startDate, endDate, "00:00", "23:59");
    }

    public Order updateItemInOrder(ItemInOrder item) {
        var i = itemInOrderService.updateItemInOrder(item);
        var order = getOrder(i.getOrder().getId());
        //order.setOriginalTotalPrice(calculateTotalPrice(order));
        calculateTotalPrices(order);
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public void deleteItemFromOrder(Long itemId) {
        var itemInOrder = itemInOrderService.getItemInOrderById(itemId);
        var order = itemInOrder.getOrder();
        itemInOrderService.deleteItemFromOrder(itemId);
        calculateTotalPrices(order);
        webSocketService.notifyExternalOrders(order);
//        order.getItems().add(itemInOrder);
//        order.setOriginalTotalPrice(calculateTotalPrice(order));
//        webSocketService.notifyExternalOrders(order);
        orderRepository.save(order);
    }

    public Order checkIfEntitledForDiscount(Order order) {
        List<Discount> relevantDiscounts = discountService.getRelevantDiscountsForCurrentOrder(order);
        relevantDiscounts.forEach(discount -> {
            discount.getCategories().forEach(category -> {
                var itemsInCategory = itemsInOrderByCategory(order, category);
                var numberOfItemsForDiscount = itemsInCategory.size() / (discount.getIfYouOrder() + discount.getYouGetDiscountFor());
                for (int i = 0; i < numberOfItemsForDiscount; i++) {
                    applyItemInOrderDiscount(itemsInCategory.get(i), discount.getPercent());
                }
            });
        });

        return orderRepository.save(order);
    }

    public Order membersCheckIfEntitledForDiscount(Order order) {
        List<Discount> relevantDiscounts = discountService.getAllRelevantDiscountsForCurrentOrder(order);
        relevantDiscounts.forEach(discount -> {
            discount.getCategories().forEach(category -> {
                var itemsInCategory = itemsInOrderByCategory(order, category);
                var numberOfItemsForDiscount = itemsInCategory.size() / (discount.getIfYouOrder() + discount.getYouGetDiscountFor());
                for (int i = 0; i < numberOfItemsForDiscount; i++) {
                    applyItemInOrderDiscount(itemsInCategory.get(i), discount.getPercent());
                    applyItemInOrderAdditionalDiscount(itemsInCategory.get(i), discount.getPercent());
                }
            });
        });

        return orderRepository.save(order);
    }

    private List<ItemInOrder> itemsInOrderByCategory(Order order, ItemCategory category) {
        return order.getItems()
                .stream()
                .filter(itemInOrder -> itemInOrder.getItem().getCategory().equals(category) && itemInOrder.getPrice() > 0)
                .sorted(Comparator.comparing(itemInOrder -> itemInOrder.getItem().getPrice())) // maybe MenuItem getPrice
                .collect(Collectors.toList());
    }

    private void applyItemInOrderDiscount(ItemInOrder itemInOrder, int percent) {
        itemInOrder.setPrice(itemInOrder.getItem().getPrice() * ((100 - percent) / (float) 100)); // maybe MenuItem getPrice
    }

    private void applyItemInOrderAdditionalDiscount(ItemInOrder itemInOrder, int percent) {
        itemInOrder.setPrice(itemInOrder.getPrice() * ((100 - percent) / (float) 100)); // maybe MenuItem getPrice
    }


}
