package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.entities.TakeAway;
import com.restaurant.smartfood.repostitory.TakeAwayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Transactional
@Slf4j
@Service
public class TakeAwayService {

    @Autowired
    private TakeAwayRepository takeAwayRepository;

    @Autowired
    private ItemInOrderService itemInOrderService;

    @Autowired
    private OrderService orderService;

    @Value("${timezone.name}")
    private String timezone;
    public TakeAway addTakeAway(TakeAway newTakeAway) {
        newTakeAway.setDate(LocalDate.now(ZoneId.of(timezone)));
        newTakeAway.setHour(LocalTime.now(ZoneId.of(timezone)));
        newTakeAway.setStatus(OrderStatus.ACCEPTED);
        newTakeAway.setAlreadyPaid((float) 0);
        newTakeAway.setTotalPrice((float) 0);
        var takeAwayInDB = takeAwayRepository.save(newTakeAway);
        takeAwayInDB.getItems().forEach(i -> {
            i.setOrder(takeAwayInDB);
            itemInOrderService.addItemToOrder(i);
        });
        var totalPrice = orderService.calculateTotalPrice(takeAwayInDB);
        takeAwayInDB.setTotalPrice(totalPrice);
        takeAwayInDB.setNewTotalPrice(totalPrice);
        return takeAwayRepository.save(takeAwayInDB);
    }

    public TakeAway updateTakeAway(TakeAway takeAway) {
        takeAwayRepository.findById(takeAway.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no take away with the id: " + takeAway.getId())
        );
        takeAwayRepository.updateTakeAway(takeAway.getPerson().getId(), takeAway.getId());
        return takeAway;
    }

    public void deleteTakeAway(Long orderId) {
        var takeAway = takeAwayRepository.findById(orderId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no take away with the id: " + orderId)
        );
        takeAwayRepository.delete(takeAway);
    }

    public List<TakeAway> getTakeAwaysByDates(String startDate, String endDate) {
        try {
            LocalDate localStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate localEndDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return takeAwayRepository.findByDateIsBetween(localStartDate, localEndDate);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public List<TakeAway> getTakeAwaysByMember(Long memberId) {
        return takeAwayRepository.findByPersonId(memberId);
    }

    public List<TakeAway> getActiveTakeAways() {
        return takeAwayRepository.findByStatusIsNot(OrderStatus.CLOSED);
    }

    public List<TakeAway> getTakeAwaysByStatus(OrderStatus status) {
        return takeAwayRepository.findByStatus(status);
    }
}
