package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    @GetMapping
    public List<RestaurantTable> getAllTables(){
        return tableService.getAllTables();
    }
    @GetMapping("/{id}")
    public RestaurantTable getTableById(@PathVariable("id") Integer tableId){
        return tableService.getTableById(tableId);
    }
    @PutMapping
    public RestaurantTable updateTable( @RequestBody RestaurantTable table) {
        return tableService.updateRestaurantTable(table);
    }
}
