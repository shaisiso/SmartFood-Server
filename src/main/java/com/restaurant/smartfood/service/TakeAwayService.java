package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.entities.TakeAway;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.TakeAwayRepository;
import com.restaurant.smartfood.utility.Utils;
import com.restaurant.smartfood.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TakeAwayService {

    private final TakeAwayRepository takeAwayRepository;

    private final ItemInOrderService itemInOrderService;
    private final OrderService orderService;
    private final WebSocketService webSocketService;
    private final MessageService messageService;


    public TakeAway addTakeAway(TakeAway newTakeAway) {
        newTakeAway = (TakeAway) orderService.initOrder(newTakeAway);
        newTakeAway = (TakeAway)  orderService.connectPersonToOrder(newTakeAway,newTakeAway.getPerson());
        var takeAwayInDB = takeAwayRepository.save(newTakeAway);
        takeAwayInDB.getItems().forEach(i -> {
            i.setOrder(takeAwayInDB);
            itemInOrderService.addItemToOrder(i);
        });
        orderService.calculateTotalPrices(takeAwayInDB);
        webSocketService.notifyExternalOrders(takeAwayInDB);
        messageService.sendMessages(newTakeAway.getPerson(),"New Take Away","Yor Take Away order was accepted and we will notify you when it's ready !!");
        return takeAwayRepository.save(takeAwayInDB);
    }
    public void deleteTakeAway(Long orderId) {
        var takeAway = takeAwayRepository.findById(orderId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no take away with the id: " + orderId)
        );
        takeAwayRepository.delete(takeAway);
        webSocketService.notifyExternalOrders(takeAway);
    }

    public List<TakeAway> getTakeAwaysByDates(String startDate, String endDate) {
        try {
            LocalDate localStartDate = Utils.parseToLocalDate(startDate);
            LocalDate localEndDate = Utils.parseToLocalDate(endDate);
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
