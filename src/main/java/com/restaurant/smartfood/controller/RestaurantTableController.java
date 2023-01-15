package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.security.AuthorizeEmployee;
import com.restaurant.smartfood.security.AuthorizeGeneralManager;
import com.restaurant.smartfood.security.AuthorizeManagers;
import com.restaurant.smartfood.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public RestaurantTable getTableById(@PathVariable("id") Integer tableId){
        return tableService.getTableById(tableId);
    }
    @GetMapping("/max")
    public Integer getTableMaxSize(){
        return tableService.getTableMaxSize();
    }
    @PutMapping("/busy/{tableId}/{isBusy}")
    public RestaurantTable changeTableBusy(@PathVariable("tableId") Integer tableId,@PathVariable("isBusy")Boolean isBusy) {
        return tableService.changeTableBusy(tableId,isBusy);
    }
    @PutMapping
    @AuthorizeGeneralManager
    public RestaurantTable updateTable(@Valid @RequestBody RestaurantTable table) {
        return tableService.updateRestaurantTable(table);
    }
    @DeleteMapping("/{tableId}")
    @AuthorizeGeneralManager
    public void deleteTable(@PathVariable("tableId") Integer tableId) {
        tableService.deleteTable(tableId);
    }
    @PostMapping
    @AuthorizeGeneralManager
    public RestaurantTable addTable(@Valid @RequestBody RestaurantTable table) {
       return tableService.addTable(table);
    }
}
