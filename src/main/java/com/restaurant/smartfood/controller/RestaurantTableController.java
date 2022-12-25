package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.security.AuthorizeEmployee;
import com.restaurant.smartfood.security.AuthorizeManagers;
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
    @AuthorizeEmployee
    public List<RestaurantTable> getAllTables(){
        return tableService.getAllTables();
    }
    @GetMapping("/{id}")
    @AuthorizeEmployee
    public RestaurantTable getTableById(@PathVariable("id") Integer tableId){
        return tableService.getTableById(tableId);
    }
    @PutMapping
    @AuthorizeManagers
    public RestaurantTable updateTable( @RequestBody RestaurantTable table) {
        return tableService.updateRestaurantTable(table);
    }
    @PutMapping("/busy/{tableId}/{isBusy}")
    public RestaurantTable changeTableBusy(@PathVariable("tableId") Integer tableId,@PathVariable("isBusy")Boolean isBusy) {
        return tableService.changeTableBusy(tableId,isBusy);
    }
}
