package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.DeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DeliveryServiceTest {

    @Autowired
    private DeliveryService deliveryService;

    @MockBean
    private DeliveryRepository deliveryRepository;

    @Value("${timezone.name}")
    private String timezone;

    Delivery delivery;

    @BeforeEach
    void setUp() {
        Person p = Person.builder()
                .name("John Terry")
                .phoneNumber("0526262626")
                .email("JohnTerry26@gmail.com")
                .address(Address.builder()
                        .city("London")
                        .houseNumber(26)
                        .streetName("Stamford")
                        .build())
                .build();
        delivery = Delivery.builder()
                .hour(LocalTime.now(ZoneId.of(timezone)))
                .person(p)
                .id(1L)
                .date(LocalDate.now(ZoneId.of(timezone)))
                .originalTotalPrice(Float.valueOf("50"))
                .totalPriceToPay(Float.valueOf("50"))
                .status(OrderStatus.ACCEPTED)
                .alreadyPaid(Float.valueOf("0"))
                .build();

        Mockito.when(deliveryRepository.findById(delivery.getId())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("update delivery the does not exist")
    void updateDelivery() {
        assertThrows(ResourceNotFoundException.class,() -> {
            deliveryService.updateDelivery(delivery);
        });
    }
}