package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Address;
import com.restaurant.smartfood.entities.Delivery;
import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DeliveryServiceTest {
    @Autowired
    private DeliveryService deliveryService;
    @MockBean
    private DeliveryRepository deliveryRepository;

    private Delivery delivery;

    @BeforeEach
   public void setUp() {
        var p = Person.builder()
                .name("John Terry")
                .phoneNumber("0526262626")
                .email("JohnTerry26@gmail.com")
                .address(Address.builder()
                        .city("London")
                        .houseNumber(26)
                        .streetName("Stamford")
                        .build())
                .build();
        var price = 50f;
        delivery = Delivery.builder()
                .hour(LocalTime.now())
                .person(p)
                .id(1L)
                .date(LocalDate.now())
                .originalTotalPrice(price)
                .totalPriceToPay(price)
                .status(OrderStatus.ACCEPTED)
                .alreadyPaid(price)
                .build();

        Mockito.doReturn(Optional.empty()).when(deliveryRepository).findById(delivery.getId());
    }

    @Test
    @DisplayName("update delivery the does not exist")
    public  void updateDelivery() {
        var expectedException =ResourceNotFoundException.class;
        assertThrows(expectedException, () -> deliveryService.updateDelivery(delivery));
    }
}