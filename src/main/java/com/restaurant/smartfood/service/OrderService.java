package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.repostitory.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updatePayment(Long orderId, Float amount) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no order with the id: " + orderId));
        order.setAlreadyPaid(order.getAlreadyPaid() + amount);
        return orderRepository.save(order);
    }
}
