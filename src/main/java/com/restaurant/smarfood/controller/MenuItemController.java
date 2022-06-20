package com.restaurant.smarfood.controller;


import com.restaurant.smarfood.entities.MenuItem;
import com.restaurant.smarfood.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/api/menu")
public class MenuItemController {

	@Autowired
	private MenuItemService menuItemService;
	
	@PostMapping
	public MenuItem addItem(@RequestBody MenuItem item) {
		return menuItemService.addItem(item);
	}
	
	
	@GetMapping
	public List<MenuItem>  getMenu() {
		return menuItemService.getMenu();
	}
	
	@GetMapping("/{id}")
	public MenuItem getItemByID(@PathVariable("id") Long itemId) throws NotFoundException {
		return menuItemService.findItemById(itemId);
	}
}
