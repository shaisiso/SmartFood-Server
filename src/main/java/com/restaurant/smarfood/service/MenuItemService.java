package com.restaurant.smarfood.service;


import com.restaurant.smarfood.entities.MenuItem;
import com.restaurant.smarfood.repostitory.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository itemRepository;

    public MenuItem addItem(MenuItem item) {
        return itemRepository.save(item);
    }

    public MenuItem findItemById(Long id) throws ResponseStatusException {
        return itemRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Id was not found"));
    }

    public List<MenuItem> getMenu() {
        return itemRepository.findAll();
    }


}
