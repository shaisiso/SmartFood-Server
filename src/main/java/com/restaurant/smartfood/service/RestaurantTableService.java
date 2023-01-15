package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.repostitory.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
//@Getter
public class RestaurantTableService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final OrderOfTableService orderOfTableService;
    private final WaitingListService waitingListService;

    @Autowired
    public RestaurantTableService(@Lazy OrderOfTableService orderOfTableService, RestaurantTableRepository restaurantTableRepository,
                                  WaitingListService waitingListService) {
        this.orderOfTableService = orderOfTableService;
        this.restaurantTableRepository = restaurantTableRepository;
        this.waitingListService = waitingListService;
    }

    public RestaurantTable updateRestaurantTable(RestaurantTable restaurantTable) {
        var table = getTable(restaurantTable.getTableId());
        boolean checkWaitingList=false;
        if (restaurantTable.getNumberOfSeats() != null) {
            if (restaurantTable.getNumberOfSeats() > table.getNumberOfSeats())
                checkWaitingList = true;
            table.setNumberOfSeats(restaurantTable.getNumberOfSeats());
        }
        if (restaurantTable.getIsBusy() != null)
            table.setIsBusy(restaurantTable.getIsBusy());

        var tableInDB = restaurantTableRepository.save(table);
        if (checkWaitingList)
            waitingListService.checkAllWaitingLists();

        return tableInDB;
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
        var table = getTableById(tableId);
        if (!isBusy) {
            orderOfTableService.optionalActiveTableOrder(tableId).ifPresent(to -> {
                if (to.getItems().isEmpty()) {
                    orderOfTableService.deleteOrderOfTable(to.getId());
                } else {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Table " + tableId + " has active order, please close the order before");
                }
            });
        }
        table.setIsBusy(isBusy);
        return restaurantTableRepository.save(table);
    }

    public void deleteTable(Integer tableId) {
        restaurantTableRepository.delete(getTable(tableId));
    }

    public RestaurantTable addTable(RestaurantTable table) {
        table.setIsBusy(false);
        var tableInDB = restaurantTableRepository.save(table);
        waitingListService.checkAllWaitingLists();
        return tableInDB;
    }

    public Integer getTableMaxSize() {
        return Collections.max(getAllTables(), Comparator.comparing(RestaurantTable::getNumberOfSeats))
                .getNumberOfSeats();
    }
}
