package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.repostitory.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private ItemInOrderService itemInOrderService;
    @MockBean
    private DiscountService discountService;
    private MenuItem item;
    private Order order;

    @BeforeEach
    void setUp() {
        item = MenuItem.builder()
                .name("fish")
                .category(ItemCategory.MAIN_DISHES)
                .price(50.0f)
                .build();

        order = Order.builder()
                .id(1L)
                .date(LocalDate.now())
                .hour(LocalTime.now())
                .items(Arrays.asList(ItemInOrder.buildFromItem(item), ItemInOrder.buildFromItem(item)))
                .build();

        Mockito.doReturn(order).when(orderRepository).save(order);
        Mockito.doReturn(Optional.of(order)).when(orderRepository).findById(order.getId());
        Mockito.doReturn(new ArrayList<>()).when(discountService).getDateRelevantDiscountsForOrder(order);
    }

    @Test
    void calculateTotalPrices() {
        orderService.calculateTotalPrices(order);
        assertEquals(order.getOriginalTotalPrice(), item.getPrice() + item.getPrice());
    }

    @Test
    void deleteItemsListFromOrder_EmptyList() {
        var expectedException = BadRequestException.class;
        assertThrows(expectedException, () -> orderService.deleteItemsListFromOrder(new ArrayList<>()));
    }

    @Test
    void paymentNegativeAmount() {
        var expectedException = BadRequestException.class;
        assertThrows(expectedException, () -> orderService.payment(1L,-10f));
    }
    @Test
    void paymentAmountBiggerThenActual() {
        order.setOriginalTotalPrice(item.getPrice()+item.getPrice());
        order.setTotalPriceToPay(item.getPrice()+item.getPrice());
        order.setAlreadyPaid(item.getPrice());
        var amountToPay = item.getPrice()+item.getPrice();
        var expectedException = BadRequestException.class;
        assertThrows(expectedException, () -> orderService.payment(1L,amountToPay));
    }
}