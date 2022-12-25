package com.restaurant.smartfood.service;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.repostitory.ItemInOrderRepository;
import com.restaurant.smartfood.websocket.WebSocketService;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Transactional
@Service
public class ItemInOrderService {
    @Autowired
    private ItemInOrderRepository itemInOrderRepository;
    @Autowired
    private WebSocketService webSocketService;

    public ItemInOrder addItemToOrder(ItemInOrder itemInOrder) {
        itemInOrder.setPrice(itemInOrder.getItem().getPrice());
        return itemInOrderRepository.save(itemInOrder);
    }

    public List<ItemInOrder> addListOfItemsToOrder(List<ItemInOrder> itemsInOrder, Order order) {
        for (var itemInOrder : itemsInOrder) {
            itemInOrder.setOrder(order);
            itemInOrder.setPrice(itemInOrder.getItem().getPrice());
        }

        return itemInOrderRepository.saveAll(itemsInOrder);
    }

    public ItemInOrder updateItemInOrder(ItemInOrder item) {
        var i = itemInOrderRepository.findById(item.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no itemInOrder with this id" + item.getId()));
        item.setOrder(i.getOrder());
        if (item.getPrice() == null)
            item.setPrice(i.getPrice());
        return itemInOrderRepository.save(item);
    }

    public void deleteItemFromOrder(Long itemId) {
        var i = itemInOrderRepository.findById(itemId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no itemInOrder with the id" + itemId));
        itemInOrderRepository.delete(i);
        webSocketService.notifyExternalOrders(i.getOrder());
    }

    public void deleteItemsListFromOrder(List<Long> itemsInOrderId) {
        for (var id : itemsInOrderId) {
            itemInOrderRepository.findById(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "There is no itemInOrder with the id" + id));
        }
        itemInOrderRepository.deleteAllById(itemsInOrderId);
        itemInOrderRepository.flush();
    }
}
