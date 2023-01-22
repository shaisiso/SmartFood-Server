package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MenuItemServiceTest {

    @Autowired
    private MenuItemService menuItemService;

    @MockBean
    private MenuItemRepository menuItemRepository;

    MenuItem carpaccio;

    @BeforeEach
    void setUp() {
        carpaccio = MenuItem.builder()
                .itemId(1L)
                .name("Beef Carpaccio")
                .price((float) 80)
                .description("Traditional Italian appetizer consisting of raw beef sliced paper-thin, drizzled with olive oil and lemon juice, and finished with capers and onions")
                .category(ItemCategory.STARTERS)
                .build();

        Mockito.when(menuItemRepository.findById(carpaccio.getItemId())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("find item that doesn't exist")
    void findItemById() {
        assertThrows(ResourceNotFoundException.class,() -> {
            menuItemService.findItemById(carpaccio.getItemId());
        });
    }
}