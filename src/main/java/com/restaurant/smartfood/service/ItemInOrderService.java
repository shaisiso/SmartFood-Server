package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.ItemInOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@Slf4j
public class ItemInOrderService {
    @Autowired
    private ItemInOrderRepository itemInOrderRepository;


    public ItemInOrder addItemToOrder(ItemInOrder itemInOrder) {
        itemInOrder.setPrice(itemInOrder.getItem().getPrice());
        return itemInOrderRepository.save(itemInOrder);
    }

    public List<ItemInOrder> addListOfItemsToOrder(List<ItemInOrder> itemsInOrder, Order order) {
        for (ItemInOrder itemInOrder : itemsInOrder) {
            itemInOrder.setOrder(order);
            itemInOrder.setPrice(itemInOrder.getItem().getPrice());
        }

        return itemInOrderRepository.saveAll(itemsInOrder);
    }

    public ItemInOrder updateItemInOrder(ItemInOrder item) {
        ItemInOrder i = getItemInOrderById(item.getId());
        item.setOrder(i.getOrder());
        if (item.getPrice() == null)
            item.setPrice(i.getPrice());
        return itemInOrderRepository.save(item);
    }

    public ItemInOrder getItemInOrderById(Long id) {
        if (id == null)
            throw new ResourceNotFoundException("Item in Order id is missing");
        return itemInOrderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("There is no item in order with the id " + id));
    }

    public void deleteItemFromOrder(Long itemId) {
        log.debug("deleteItemFromOrder: " + itemId);
        ItemInOrder i = getItemInOrderById(itemId);
        itemInOrderRepository.delete(i);
        itemInOrderRepository.flush();

    }


    public void deleteItemsListFromOrder(List<Long> itemsInOrderId) {
        for (Long id : itemsInOrderId) {
            log.info("deleteItemsListFromOrder: " + itemsInOrderId);
            itemInOrderRepository.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException("There is no itemInOrder with the id" + id));
        }
        itemInOrderRepository.deleteAllById(itemsInOrderId);
        itemInOrderRepository.flush();

    }
}
