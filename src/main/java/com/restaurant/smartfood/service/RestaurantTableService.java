package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.repostitory.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class RestaurantTableService {

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    public RestaurantTable updateRestaurantTable(RestaurantTable restaurantTable) {
        getTable(restaurantTable.getTableId());
        return restaurantTableRepository.save(restaurantTable);
    }
    public RestaurantTable getTable(Integer id) {
        return restaurantTableRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no table with the id: "+id));
    }
}