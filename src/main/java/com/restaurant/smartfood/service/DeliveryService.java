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

    @Value("${timezone.name}")
    private String timezone;

    public Delivery addDelivery(Delivery newDelivery) {
        var d = (Delivery) orderService.initOrder(newDelivery);
        var deliveryInDB = deliveryRepository.save(d);
        deliveryInDB.getItems().forEach(i -> {
            i.setOrder(deliveryInDB);
            itemInOrderService.addItemToOrder(i);
        });
        deliveryInDB.setTotalPrice(orderService.calculateTotalPrice(deliveryInDB));

        newDelivery = deliveryRepository.save(deliveryInDB);
        webSocketService.notifyNewDelivery(newDelivery);
        return newDelivery;
    }

    public Delivery updateDelivery(Delivery delivery) { //only updates delivery guy and person details
        deliveryRepository.findById(delivery.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no delivery with the id: " + delivery.getId())
        );
        deliveryRepository.updateDelivery(delivery.getDeliveryGuy().getId(),
                delivery.getPersonDetails().getId(), delivery.getId());

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
