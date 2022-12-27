package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.repostitory.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class RestaurantTableService {

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    public RestaurantTable updateRestaurantTable(RestaurantTable restaurantTable) {
        var table = getTable(restaurantTable.getTableId());
        if (restaurantTable.getNumberOfSeats() != null)
            table.setNumberOfSeats(restaurantTable.getNumberOfSeats());
        if (restaurantTable.getIsBusy()!= null)
            table.setIsBusy(restaurantTable.getIsBusy());
        return restaurantTableRepository.save(table);
    }

    public RestaurantTable getTable(Integer id) {
        return restaurantTableRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no table with the id: " + id));
    }

    public List<RestaurantTable> getAllTables() {
        return restaurantTableRepository.findAll();
    }

    public RestaurantTable getTableById(Integer tableId) {
        return restaurantTableRepository.findById(tableId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no table with table id : " + tableId));
    }

    public RestaurantTable changeTableBusy(Integer tableId, Boolean isBusy) {
        var table =getTableById(tableId);
        table.setIsBusy(isBusy);
        return restaurantTableRepository.save(table);
    }
}
