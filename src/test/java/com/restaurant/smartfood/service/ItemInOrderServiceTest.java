package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.ItemInOrderRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ItemInOrderServiceTest {
    @Autowired
    private ItemInOrderService itemInOrderService;
    @MockBean
    private ItemInOrderRepository itemInOrderRepository;
    private MenuItem item;
    @BeforeEach
    void setUp() {
        item= MenuItem.builder()
                .price(50f)
                .category(ItemCategory.STARTERS)
                .name("Salad")
                .itemId(1L)
                .build();

        Mockito.doReturn(Optional.empty()).when(itemInOrderRepository).findById(2L);

    }
    @Test
    void getItemInOrderByIdNotFound() {
        var expectedException = ResourceNotFoundException.class;
        assertThrows(expectedException,()->itemInOrderService.getItemInOrderById(2L));
    }
    @Test
    void updateItemInOrder(){
        var itemInOrder = ItemInOrder.buildFromItem(item);
        var updatedItemInOrder =ItemInOrder.buildFromItem(item);
        var newPrice =40f;
        itemInOrder.setId(1L);
        updatedItemInOrder.setId(1L);
        updatedItemInOrder.setPrice(newPrice);
        Mockito.doReturn(Optional.of(itemInOrder)).when(itemInOrderRepository).findById(itemInOrder.getId());
        Mockito.doReturn(updatedItemInOrder).when(itemInOrderRepository).save(itemInOrder);

        var actualResult = itemInOrderService.updateItemInOrder(itemInOrder);

        assertEquals(actualResult.getPrice(),newPrice);
    }
}