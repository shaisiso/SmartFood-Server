package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeRole;
import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.repostitory.MenuItemRepository;
import com.restaurant.smartfood.repostitory.ShiftRepository;
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
    private MenuItem item;

    @BeforeEach
    void setUp() {
        item = new MenuItem();
        Mockito.doReturn(Optional.of(item)).when(menuItemRepository).findById(1L);
    }

    @Test
    void findById() {
        // arrange
        var expectedResult = item;
        //act
        var actualResult = menuItemService.findItemById(1L);
        // assert
        assertEquals(expectedResult, actualResult);
    }
}