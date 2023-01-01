package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.repostitory.DiscountRepository;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemInOrderService itemInOrderService;
    private final MemberService memberService;
    private final DiscountRepository discountRepository;
    private final MemberRepository memberRepository;
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
        var totalPrice = calculateTotalPrice(orderInDB);
        orderInDB.setOriginalTotalPrice(totalPrice);
        orderInDB.setTotalPriceToPay(totalPrice);
        return orderRepository.save(orderInDB);
    }

    public Order addItemToOrder(Long orderId, ItemInOrder item) {
        var order = getOrder(orderId);
        item.setOrder(order);
        var itemInOrder = itemInOrderService.addItemToOrder(item);
        order.getItems().add(itemInOrder);
        order.setOriginalTotalPrice(calculateTotalPrice(order));
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public Order deleteItemsListFromOrder(List<Long> itemsInOrderId) {
        if (itemsInOrderId == null || itemsInOrderId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "List of items is missing");
        itemInOrderService.deleteItemsListFromOrder(itemsInOrderId);
        var order = getOrder(itemsInOrderId.get(0)); // TODO : CHANGE THIS IS NOT THE REAL ORDER !!!!
        calculateTotalPrice(order);
        webSocketService.notifyExternalOrders(order);
        return order;
    }

    public Order addItemsListToOrder(Long orderId, List<ItemInOrder> items) {
        var order = getOrder(orderId);
        var itemsInOrder = itemInOrderService.addListOfItemsToOrder(items, order);
        order.getItems().addAll(itemsInOrder);
        order.setOriginalTotalPrice(calculateTotalPrice(order));
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public float calculateTotalPrice(Order order) {
        var price = order.getItems().stream().map(i -> i.getPrice())
                .reduce((float) 0, Float::sum);
        order.setTotalPriceToPay(price);
        order.setOriginalTotalPrice(price);
        return price;
    }

    public Order payment(Long orderId, Float amount) {
        var order = getOrder(orderId);
        if (order.getOriginalTotalPrice() < amount + order.getAlreadyPaid())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't pay more then the remaining amount.");
        order.setAlreadyPaid(order.getAlreadyPaid() + amount);
        if (order.getAlreadyPaid().equals(order.getOriginalTotalPrice())) {
           orderOfTableService.closeIfOrderOfTable(order);
        }
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
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

    public Order applyMemberDiscount(Long orderId, Member member) {
        var order = getOrder(orderId);
        memberService.getMemberByPhoneNumber(member.getPhoneNumber());
        order.setOriginalTotalPrice(order.getOriginalTotalPrice() * (float) 0.9);

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
        order.setOriginalTotalPrice(calculateTotalPrice(order));
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public void deleteItemFromOrder(Long itemId) {
        itemInOrderService.deleteItemFromOrder(itemId);
        //  TODO : Need to calculate total price and update order !!
    }

    public Order checkIfEntitledToDiscount(Long orderId, String phoneNumber) {
        var order = getOrder(orderId);
        boolean isMember = false;
        var category = "";
        var numberOfItems = 0;
        var oldPrice = 0.0;
        List<ItemInOrder> items;

        if (phoneNumber != null)
            isMember = memberRepository.findByPhoneNumber(phoneNumber).isPresent();

        List<Discount> discounts = discountRepository.findByStartDateIsBetweenAndStartHourIsLessThanEqualAndEndHourIsGreaterThanEqual
                (order.getDate(), order.getDate(), order.getHour(), order.getHour());

        for (var d : discounts)
            if (!d.getDays().contains(LocalDate.now().getDayOfWeek()))
                discounts.remove(d);

        if (!isMember)
            for (var d : discounts)
                if (d.getForMembersOnly())
                    discounts.remove(d);

        for (var discount : discounts) {
            for (var c : discount.getCategories()) {
                items = howManyByCategory(order, c);
                numberOfItems = items.size() / (discount.getIfYouOrder() + discount.getYouGetDiscountFor()); // the number of items that get discount
                for (int i = 0; i < numberOfItems; i++)
                    applyItemInOrderDiscount(items.get(i), discount.getPercent());
            }
        }
        return orderRepository.save(order);
    }

    private List<ItemInOrder> howManyByCategory(Order order, ItemCategory category) {
        var items = new ArrayList<ItemInOrder>();
        for (var i : order.getItems()) {
            if (i.getItem().getCategory().equals(category) && i.getPrice() > 0)
                items.add(i);
        }
        items.sort(Comparator.comparing(ItemInOrder::getPrice));
        return items;
    }

    private ItemInOrder applyItemInOrderDiscount(ItemInOrder itemInOrder, int percent) {
        itemInOrder.setPrice(itemInOrder.getPrice() * ((100 - percent) / (float) 100));
        return itemInOrder;
    }


}
