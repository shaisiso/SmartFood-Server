package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.repostitory.DeliveryRepository;
import com.restaurant.smartfood.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Transactional
@Slf4j
@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private ItemInOrderService itemInOrderService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private PersonService personService;

    @Value("${timezone.name}")
    private String timezone;

    public Delivery addDelivery(Delivery newDelivery) {
        var d = (Delivery) orderService.initOrder(newDelivery);
        d= connectPersonToDelivery(d);
        var deliveryInDB = deliveryRepository.save(d);
        deliveryInDB.getItems().forEach(i -> {
            i.setOrder(deliveryInDB);
            itemInOrderService.addItemToOrder(i);
        });
        var totalPrice = orderService.calculateTotalPrice(deliveryInDB);
        deliveryInDB.setTotalPrice(totalPrice);
        deliveryInDB.setNewTotalPrice(totalPrice);
        var dToReturn = deliveryRepository.save(deliveryInDB);
        webSocketService.notifyExternalOrders(dToReturn);
        return dToReturn;
    }

    public Delivery updateDelivery(Delivery delivery) { //only updates delivery guy and person details
        deliveryRepository.findById(delivery.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no delivery with the id: " + delivery.getId())
        );
        var d = connectPersonToDelivery(delivery);
        var deliveryGuyId = d.getDeliveryGuy() != null ? d.getDeliveryGuy().getId() : null;
        deliveryRepository.updateDelivery(deliveryGuyId,  d.getPersonDetails().getId(), d.getId());
        return delivery;
    }

    private Delivery connectPersonToDelivery(Delivery delivery) {
        if (delivery.getPersonDetails().getId() == null) {
            personService.getOptionalPersonByPhone(delivery.getPersonDetails().getPhoneNumber())
                    .ifPresentOrElse(p -> {
                                var person = delivery.getPersonDetails();
                                person.setId(p.getId());
                                personService.savePerson(person);
                                delivery.setPersonDetails(person);
                            },
                            () -> {
                                var p = personService.savePerson(delivery.getPersonDetails());
                                delivery.setPersonDetails(p);
                            });
        }
        return delivery;
    }

    public void deleteDelivery(Long orderId) {
        var delivery = deliveryRepository.findById(orderId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no delivery with the id: " + orderId)
        );
        deliveryRepository.delete(delivery);
    }

    public List<Delivery> getDeliveriesByDates(String startDate, String endDate) {
        try {
            LocalDate localStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate localEndDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return deliveryRepository.findByDateIsBetween(localStartDate, localEndDate);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public List<Delivery> getDeliveriesByMember(Long memberId) {
        return deliveryRepository.findByPersonDetailsId(memberId);
    }

    public List<Delivery> getDeliveriesByDeliveryGuy(Long id) {
        return deliveryRepository.findByDeliveryGuyId(id);
    }

    public List<Delivery> getDeliveriesByStatus(OrderStatus status) {

        return deliveryRepository.findByStatus(status);
    }

    public List<Delivery> getActiveDeliveries() {
        return deliveryRepository.findByStatusIsNot(OrderStatus.CLOSED);
    }

    public void saveAll(List<Delivery> deliveries) {
        deliveryRepository.saveAll(deliveries);
    }
}
