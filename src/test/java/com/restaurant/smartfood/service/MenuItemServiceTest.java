package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.MenuItemRepository;
import com.restaurant.smartfood.repostitory.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MenuItemServiceTest {
    @Autowired
    private MenuItemService menuItemService;
    @MockBean
    private MenuItemRepository menuItemRepository;
    private MenuItem carpaccio;

    @BeforeEach
    void setUp() {
        carpaccio = MenuItem.builder()
                .itemId(1L)
                .name("Beef Carpaccio")
                .price(80F)
                .description("Traditional Italian appetizer consisting of raw beef sliced paper-thin, drizzled with olive oil and lemon juice, and finished with capers and onions")
                .category(ItemCategory.STARTERS)
                .build();
        Mockito.doReturn(Optional.of(carpaccio)).when(menuItemRepository).findById(1L);
        Mockito.doReturn(Optional.empty()).when(menuItemRepository).findById(2L);
    }

    @Test
    void findById() {
        // arrange
        var expectedResult = carpaccio;
        //act
        var actualResult = menuItemService.findItemById(1L);
        // assert
        assertEquals(expectedResult, actualResult);
    }
    @Test
    @DisplayName("find item that doesn't exist")
    void findItemByIdNotFound() {
        var expectedException = ResourceNotFoundException.class;
        assertThrows(expectedException,() ->   menuItemService.findItemById(2L));
    }
}