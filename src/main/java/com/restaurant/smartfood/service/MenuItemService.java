package com.restaurant.smartfood.service;


import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public MenuItem findItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("There is no item with item id:" + id));
    }

    public List<MenuItem> getMenu() {
        return itemRepository.findAll();
    }


    public Map<String, List<MenuItem>> getCategorizedMenu() {
        List<MenuItem> menu = itemRepository.findAll();
        List<ItemCategory> categories = ItemCategory.stream()
                .collect(Collectors.toList());
        Map<String, List<MenuItem>> categorizedMenus = new LinkedHashMap<>();
        categories.forEach(category -> {
            List<MenuItem> itemsForCategory = menu.stream()
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
        MenuItem item = findItemById(itemId);
        itemRepository.delete(item);
    }

    public MenuItem getItemByName(String name) {
        return itemRepository.findByName(name).orElseThrow(() ->
                new ResourceNotFoundException("There is no item with the name: " + name));
    }

    public List<MenuItem> getItemByCategory(ItemCategory category) {
        return itemRepository.findByCategory(category);
    }
}
