package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.repostitory.RestaurantTableRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@Getter
public class RestaurantTableService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final  OrderOfTableService orderOfTableService;
    @Autowired
    public RestaurantTableService(@Lazy OrderOfTableService orderOfTableService, RestaurantTableRepository restaurantTableRepository){
        this.orderOfTableService=orderOfTableService;
        this.restaurantTableRepository = restaurantTableRepository;
    }

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
        if (isBusy==false ){
            orderOfTableService.optionalActiveTableOrder(tableId).ifPresent(to->{
                if (to.getItems().isEmpty()){
                    orderOfTableService.deleteOrderOfTable(to.getId());
                }else{
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Table " +tableId+" has active order, please close the order before");
                }
            });
        }
        table.setIsBusy(isBusy);
        return restaurantTableRepository.save(table);
    }
}
