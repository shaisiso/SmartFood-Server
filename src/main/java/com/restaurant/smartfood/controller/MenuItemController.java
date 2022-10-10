package com.restaurant.smartfood.controller;


import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.service.MenuItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/menu")
@Slf4j
public class MenuItemController {

	@Autowired
	private MenuItemService menuItemService;
	
	@GetMapping("/categories")
	public List<String> getCategories(){
		return ItemCategory.stream().
				map(category->category.toString())
				.collect(Collectors.toList());
	}
	@GetMapping
	public List<MenuItem>  getMenu() {
		log.trace("get menu");
		return menuItemService.getMenu();
	}
	
	@GetMapping("/{id}")
	public MenuItem getItemByID(@PathVariable("id") Long itemId) throws NotFoundException {
		return menuItemService.findItemById(itemId);
	}
	@PostMapping
	public MenuItem addItem(@Valid @RequestBody MenuItem item) {
		log.info("ADD Item");
		return menuItemService.addItem(item);
	}
}
