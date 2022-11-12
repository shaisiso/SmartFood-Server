package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.repostitory.ItemInOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemInOrderService {
    @Autowired
    private ItemInOrderRepository itemInOrderRepository;
    public ItemInOrder save(ItemInOrder itemInOrder) {
        itemInOrder.setPrice(itemInOrder.getItem().getPrice());
        return itemInOrderRepository.save(itemInOrder);
    }
}
