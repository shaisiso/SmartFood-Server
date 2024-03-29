package com.restaurant.smartfood.controller;


import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.security.AuthorizeGeneralManager;
import com.restaurant.smartfood.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/menu")
public class MenuItemController {
    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping
    @AuthorizeGeneralManager
    public MenuItem addItem(@Valid @RequestBody MenuItem item) {
        return menuItemService.addItem(item);
    }

    @PutMapping
    @AuthorizeGeneralManager
    public MenuItem updateItem(@Valid @RequestBody MenuItem updatedItem) {
        return menuItemService.updateItem(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    @AuthorizeGeneralManager
    public void deleteItem(@PathVariable("itemId") Long itemId) {
        menuItemService.deleteMenuItem(itemId);
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return ItemCategory.getCategoriesNames();
    }

    @GetMapping
    public List<MenuItem> getMenu() {
        return menuItemService.getMenu();
    }

    @GetMapping("/categorized")
    public Map<String, List<MenuItem>> getCategorizedMenu() {
        return menuItemService.getCategorizedMenu();
    }

    @GetMapping("/name/{name}")
    public MenuItem getItemByName(@PathVariable("name") String name) {
        return menuItemService.getItemByName(name);
    }

    @GetMapping("/category/{category}")
    public List<MenuItem> getItemByCategory(@PathVariable("category") ItemCategory category) {
        return menuItemService.getItemByCategory(category);
    }

    @GetMapping("/{id}")
    public MenuItem getItemByID(@PathVariable("id") Long itemId) {
        return menuItemService.findItemById(itemId);
    }
}
