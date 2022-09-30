package com.restaurant.smartfood.controller;


import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.service.MenuItemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/menu")
@Log4j2
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
