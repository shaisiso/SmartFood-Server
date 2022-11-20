package com.restaurant.smartfood.service;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.repostitory.ItemInOrderRepository;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Transactional
@Service
public class ItemInOrderService {
    @Autowired
    private ItemInOrderRepository itemInOrderRepository;
    public ItemInOrder addItemToOrder(ItemInOrder itemInOrder) {
        itemInOrder.setPrice(itemInOrder.getItem().getPrice());
        return itemInOrderRepository.save(itemInOrder);
    }

    public ItemInOrder updateItemInOrder(ItemInOrder item) {
        itemInOrderRepository.findById(item.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no itemInOrder with this id"+item.getId()));
        return itemInOrderRepository.save(item);
    }
    public void deleteItemFromOrder(Long itemId) {
        var i = itemInOrderRepository.findById(itemId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no itemInOrder with the id"+itemId));
        itemInOrderRepository.delete(i);
    }
}
