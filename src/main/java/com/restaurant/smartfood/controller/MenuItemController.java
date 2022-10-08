package com.restaurant.smartfood.controller;


import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.service.MenuItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/menu")
@Slf4j
public class MenuItemController {

	@Autowired
	private MenuItemService menuItemService;
	
	@PostMapping
	public MenuItem addItem(@Valid @RequestBody MenuItem item) {
		log.info("ADD Item");
		return menuItemService.addItem(item);
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
}
