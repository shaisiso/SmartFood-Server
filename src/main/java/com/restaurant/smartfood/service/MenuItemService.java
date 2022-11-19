package com.restaurant.smartfood.service;


import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.repostitory.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class MenuItemService {

    @Autowired
    private MenuItemRepository itemRepository;

    public MenuItem addItem(MenuItem item) {
        return itemRepository.save(item);
    }

    public MenuItem findItemById(Long id) throws ResponseStatusException {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no item with item id" + id));
    }

    public List<MenuItem> getMenu() {
        return itemRepository.findAll();
    }


    public Map<String, List<MenuItem>> getCategorizedMenu() {
        var menu = itemRepository.findAll();
        var categories = ItemCategory.stream()
                .collect(Collectors.toList());
        Map<String, List<MenuItem>> categorizedMenus = new LinkedHashMap<>();
        categories.forEach(category -> {
            var itemsForCategory = menu.stream()
                    .filter(menuItem -> menuItem.getCategory().equals(category))
                    .collect(Collectors.toList());
            categorizedMenus.put(category.toString(), itemsForCategory);
        });
        return categorizedMenus;
    }

    public MenuItem updateItem(MenuItem updatedItem) {
        return itemRepository.save(updatedItem);
    }

    public void deleteMenuItem(Long itemId) {
        var item = findItemById(itemId);
        itemRepository.delete(item);
    }

    public MenuItem getItemByName(String name) {
        return itemRepository.findByName(name).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no item with the name: " + name));
    }

    public List<MenuItem> getItemByCategory(ItemCategory category) {
        return itemRepository.findByCategory(category);
    }
}
