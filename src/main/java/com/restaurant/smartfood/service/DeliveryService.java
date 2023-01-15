package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.DeliveryRepository;
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
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final ItemInOrderService itemInOrderService;
    private final WebSocketService webSocketService;
    private final OrderService orderService;
    private final MemberService memberService;
    private final MessageService messageService;

    public Delivery addDelivery(Delivery newDelivery) {
        var d = (Delivery) orderService.initOrder(newDelivery);
        d =(Delivery) orderService.connectPersonToOrder(d,d.getPerson());
        var deliveryInDB = deliveryRepository.save(d);
        deliveryInDB.getItems().forEach(i -> {
            i.setOrder(deliveryInDB);
            itemInOrderService.addItemToOrder(i);
        });
        orderService.calculateTotalPrices(deliveryInDB);
        var dToReturn = deliveryRepository.save(deliveryInDB);
        messageService.sendMessages(newDelivery.getPerson(),"New Delivery","Yor delivery was accepted and we will notify you when it's ready !!");
        webSocketService.notifyExternalOrders(dToReturn);
        return dToReturn;
    }

    public Delivery updateDelivery(Delivery delivery) { //only updates delivery guy and person details
        deliveryRepository.findById(delivery.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no delivery with the id: " + delivery.getId())
        );
      //  var d = connectPersonToDelivery(delivery);
        var deliveryGuyId = delivery.getDeliveryGuy() != null ? delivery.getDeliveryGuy().getId() : null;
        deliveryRepository.updateDelivery(deliveryGuyId, delivery.getId());
        webSocketService.notifyExternalOrders(delivery);
        return delivery;
    }
    public void deleteDelivery(Long orderId) {
        var delivery = deliveryRepository.findById(orderId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no delivery with the id: " + orderId)
        );
        deliveryRepository.delete(delivery);
        webSocketService.notifyExternalOrders(delivery);
    }

    public List<Delivery> getDeliveriesByDates(String startDate, String endDate) {
        try {
            LocalDate localStartDate = Utils.parseToLocalDate(startDate);
            LocalDate localEndDate = Utils.parseToLocalDate(endDate);
            return deliveryRepository.findByDateIsBetween(localStartDate, localEndDate);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public List<Delivery> getDeliveriesByMember(Long memberId) {
        memberService.getMemberById(memberId);
        return deliveryRepository.findByPersonId(memberId);
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
