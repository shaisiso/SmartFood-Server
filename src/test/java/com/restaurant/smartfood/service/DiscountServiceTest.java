package com.restaurant.smartfood.service;

import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiscountServiceTest {
    @Autowired
    private DiscountService discountService;
    @MockBean
    private DiscountRepository discountRepository;

    @BeforeEach
    void setUp() {
        Mockito.doReturn(Optional.empty()).when(discountRepository).findById(1L);
    }
    @Test
    void deleteDiscount() {
        var expectedException = ResourceNotFoundException.class;
        assertThrows(expectedException, () -> discountService.deleteDiscount(1L));
    }
}