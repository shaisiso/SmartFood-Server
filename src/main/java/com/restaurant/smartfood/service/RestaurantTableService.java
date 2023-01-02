package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.repostitory.RestaurantTableRepository;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Getter
public class RestaurantTableService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final  OrderOfTableService orderOfTableService;
    private final TableReservationRepository tableReservationRepository;
    @Value("${timezone.name}")
    private String timezone;
    @Autowired
    public RestaurantTableService(@Lazy OrderOfTableService orderOfTableService, RestaurantTableRepository restaurantTableRepository,TableReservationRepository tableReservationRepository){
        this.orderOfTableService=orderOfTableService;
        this.restaurantTableRepository = restaurantTableRepository;
        this.tableReservationRepository =tableReservationRepository;
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




    public List<RestaurantTable> findSuitableTableForNow()
    {
        // res = all table reservations for the next 2 hours
        var res = tableReservationRepository.
                findByDateIsAndHourIsBetween(LocalDate
                                .now(ZoneId.of(timezone)),
                        LocalTime.now(ZoneId.of(timezone)).minusMinutes(15),
                        LocalTime.now(ZoneId.of(timezone)).plusHours(2));

        // busyTables = the busy tables from the reservations
        List<RestaurantTable> busyTables = res.stream()
                .map(TableReservation::getTable)
                .collect(Collectors.toList());

        // freeTables = the free tables in the relevant hours
        var freeTables = restaurantTableRepository.findAll();
        freeTables.removeIf(t -> busyTables.contains(t.getTableId()));

        return freeTables; // green and yellow tables
    }
}
