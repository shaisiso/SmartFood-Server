package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.repostitory.OrderRepository;
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
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemInOrderService itemInOrderService;

    @Value("${timeZone.name}")
    private String timeZone;

    public List<Order> getAllOrders() { // TODO: order get all prints infinity
        return orderRepository.findAll();
    }

    public Order addOrder(Order order) {
        order.setDate(LocalDate.now(ZoneId.of(timeZone)));
        order.setHour(LocalTime.now(ZoneId.of(timeZone)));
        order.setStatus(OrderStatus.ACCEPTED);
        return orderRepository.save(order);
    }

    public Order addItemToOrder(Long orderId, ItemInOrder item) {
        var order = getOrder(orderId);
        item.setOrder(order);
        itemInOrderService.save(item);
        order.getItems().add(item);
        return orderRepository.save(order);
    }
    private Order totalPriceCalculation(Order order) {
        for (var item: order.getItems())
            order.setTotalPrice(order.getTotalPrice() + item.getPrice());
        return order;
    }
    public Order payment(Long orderId, Float amount) {
        var order = getOrder(orderId);
        order.setAlreadyPaid(order.getAlreadyPaid() + amount);
        return orderRepository.save(order);
    }

    public Order updateComment(Long orderId, String comment) {
        var order = getOrder(orderId);
        if (order.getOrderComment() != null)
            order.setOrderComment(order.getOrderComment().concat(". "+comment));
        else
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
        order.setTotalPrice(order.getTotalPrice() * ((100 - percent) / 100));
        return orderRepository.save(order);

    }

    public Order applyMemberDiscount(Long orderId, Member member) {
        var order = getOrder(orderId);
        order.setTotalPrice(order.getTotalPrice() * (float)0.9);
        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no order with the id: " + orderId));
    }

    public List<Order> getOrderByDates(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        if (startTime == null) {
            startTime= LocalTime.of(0,0);
            endTime= LocalTime.of(23,59);
        }
        return orderRepository.findByDateIsBetweenAndHourIsBetween(startDate, endDate, startTime, endTime);
    }

    public Order updateStatus(Long orderId, OrderStatus status) {
        var order = getOrder(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
