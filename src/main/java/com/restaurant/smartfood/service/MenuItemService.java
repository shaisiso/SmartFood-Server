package com.restaurant.smartfood.service;


import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.repostitory.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
