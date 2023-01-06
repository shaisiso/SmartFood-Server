package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.OrderStatus;
import com.restaurant.smartfood.entities.TakeAway;
import com.restaurant.smartfood.repostitory.TakeAwayRepository;
import com.restaurant.smartfood.utility.Utils;
import com.restaurant.smartfood.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
    @Autowired
    private PersonService personService;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private MemberService memberService;


    public TakeAway addTakeAway(TakeAway newTakeAway) {
        newTakeAway = (TakeAway) orderService.initOrder(newTakeAway);
        newTakeAway = connectPersonToTA(newTakeAway);
        var takeAwayInDB = takeAwayRepository.save(newTakeAway);
        takeAwayInDB.getItems().forEach(i -> {
            i.setOrder(takeAwayInDB);
            itemInOrderService.addItemToOrder(i);
        });
        orderService.calculateTotalPrices(takeAwayInDB);
        webSocketService.notifyExternalOrders(takeAwayInDB);
        return takeAwayRepository.save(takeAwayInDB);
    }

    private TakeAway connectPersonToTA(TakeAway takeAway) {
        if (takeAway.getPerson().getId() == null) {
            personService.getOptionalPersonByPhone(takeAway.getPerson().getPhoneNumber())
                    .ifPresentOrElse(p -> {
                                var person = takeAway.getPerson();
                                person.setId(p.getId());
                                takeAway.setPerson(personService.savePerson(person));
                            },
                            () -> {
                                var p = personService.savePerson(takeAway.getPerson());
                                takeAway.setPerson(p);
                            });
        } else {
            var p = personService.updatePerson(takeAway.getPerson());
            takeAway.setPerson(p);
        }
        return takeAway;
    }

    public TakeAway updateTakeAway(TakeAway takeAway) {
        takeAwayRepository.findById(takeAway.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no take away with the id: " + takeAway.getId())
        );
        var ta = connectPersonToTA(takeAway);
        takeAwayRepository.updateTakeAway(ta.getPerson().getId(), ta.getId());
        webSocketService.notifyExternalOrders(ta);
        return takeAwayRepository.findById(ta.getId()).get();
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
