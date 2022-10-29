package com.restaurant.smartfood.controller;


import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuItemController {
	private final MenuItemService menuItemService;
	

	@PostMapping
	public MenuItem addItem(@RequestBody MenuItem item) {
		return menuItemService.addItem(item);
	}
	@GetMapping("/categories")
	public List<String> getCategories(){
		return ItemCategory.getCategoriesNames();

	}
	@GetMapping
	public List<MenuItem>  getMenu() {
		return menuItemService.getMenu();
	}

	@GetMapping("/categorized")
	public Map<String,List<MenuItem>> getCategorizedMenu() {
		return menuItemService.getCategorizedMenu();
	}
	@GetMapping("/{id}")
	public MenuItem getItemByID(@PathVariable("id") Long itemId) throws NotFoundException {
		return menuItemService.findItemById(itemId);
	}
}
