package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.repostitory.OrderRepository;
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
import java.util.List;

@Service
@Transactional
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemInOrderService itemInOrderService;

    @Value("${timezone.name}")
    private String timezone;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order addOrder(Order order) {
        order.setDate(LocalDate.now(ZoneId.of(timezone)));
        order.setHour(LocalTime.now(ZoneId.of(timezone)));
        order.setStatus(OrderStatus.ACCEPTED);
        order.setAlreadyPaid((float) 0);
        order.setTotalPrice((float) 0);
        var orderInDB = orderRepository.save(order);
        orderInDB.getItems().forEach(i -> {
            i.setOrder(orderInDB);
            itemInOrderService.save(i);
        });
        orderInDB.setTotalPrice(calculateTotalPrice(orderInDB));
        return orderRepository.save(orderInDB);
    }

    public Order addItemToOrder(Long orderId, ItemInOrder item) {
        var order = getOrder(orderId);
        item.setOrder(order);
        itemInOrderService.save(item);
        order.getItems().add(item);
        order.setTotalPrice(calculateTotalPrice(order));
        return orderRepository.save(order);
    }

    public float calculateTotalPrice(Order order) {
        return order.getItems().stream().map(i -> i.getPrice())
                .reduce((float) 0, Float::sum);
    }

    public Order payment(Long orderId, Float amount) {
        var order = getOrder(orderId);
        if (order.getTotalPrice() < amount + order.getAlreadyPaid())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't pay more then the remaining amount.");
        order.setAlreadyPaid(order.getAlreadyPaid() + amount);
        if (order.getAlreadyPaid().equals(order.getTotalPrice()))
            order.setStatus(OrderStatus.CLOSED);
        return orderRepository.save(order);
    }

    public Order updateComment(Long orderId, String comment) {
        var order = getOrder(orderId);
        order.setOrderComment(comment);
        return orderRepository.save(order);
    }

    public Order updateTotalPrice(Long orderId, Float amount) {
        var order = getOrder(orderId);
        if (amount > order.getTotalPrice())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The amount is bigger than the total price");
        order.setTotalPrice(order.getTotalPrice() - amount);
        return orderRepository.save(order);
    }

    public Order applyDiscount(Long orderId, Integer percent) {
        var order = getOrder(orderId);
        if (percent < 0 || percent > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "discount percent invalid.");
        order.setTotalPrice(order.getTotalPrice() * ((100 - percent) / (float) 100));
        return orderRepository.save(order);

    }

    public Order applyMemberDiscount(Long orderId, Member member) {
        var order = getOrder(orderId);
        order.setTotalPrice(order.getTotalPrice() * (float) 0.9);
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
        return orderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.delete(getOrder(orderId));
    }

    public List<Order> getOrdersByDates(String startDate, String endDate) {
       return getOrdersByDatesAndHours(startDate,endDate,"00:00","23:59");
    }
}
