package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.exception.UnprocessableEntityException;
import com.restaurant.smartfood.repostitory.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class QRServiceTest {
    @Autowired
    private QRService QRService;
    @MockBean
    private RestaurantTableService tableService;

    @BeforeEach
    void setUp() {
        Mockito.doReturn(new RestaurantTable()).when(tableService).getTableById(1);
    }

    @Test
    void generateTokenForQR() {
        var expectedException = UnprocessableEntityException.class;
        assertThrows(expectedException, () -> QRService.generateTokenForQR(1,0));
    }
    @Test
    void verifyToken(){
        var notValidToken ="asdasdjh21hesdf";
        var expectedException = BadCredentialsException.class;
        assertThrows(expectedException, () -> QRService.verifyToken(notValidToken));
    }
}