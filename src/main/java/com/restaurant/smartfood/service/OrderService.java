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
import java.util.stream.Stream;

@Service
@Transactional
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
        order.setAlreadyPaid((float)0);
        order.setTotalPrice((float)0);
        var orderInDB = orderRepository.save(order);
        orderInDB.getItems().forEach(i ->{
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
    private float calculateTotalPrice(Order order) {
       return order.getItems().stream().map(i->i.getPrice())
                .reduce((float)0,Float::sum);
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

    public void deleteOrder(Long orderId) {
        orderRepository.delete(getOrder(orderId));
    }
}
