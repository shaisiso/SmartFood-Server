package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.DeliveryRepository;
import com.restaurant.smartfood.utility.Utils;
import com.restaurant.smartfood.utility.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Transactional
@Slf4j
@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final ItemInOrderService itemInOrderService;
    private final WebSocketService webSocketService;
    private final OrderService orderService;
    private final MemberService memberService;
    private final MessageService messageService;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository, ItemInOrderService itemInOrderService, WebSocketService webSocketService, OrderService orderService, MemberService memberService, MessageService messageService) {
        this.deliveryRepository = deliveryRepository;
        this.itemInOrderService = itemInOrderService;
        this.webSocketService = webSocketService;
        this.orderService = orderService;
        this.memberService = memberService;
        this.messageService = messageService;
    }

    public Delivery addDelivery(Delivery newDelivery) {
        Delivery d = (Delivery) orderService.initOrder(newDelivery);
        d = (Delivery) orderService.connectPersonToOrder(d, d.getPerson());
        Delivery deliveryInDB = deliveryRepository.save(d);
        deliveryInDB.getItems().forEach(i -> {
            i.setOrder(deliveryInDB);
            itemInOrderService.addItemToOrder(i);
        });
        orderService.calculateTotalPrices(deliveryInDB);
        Delivery dToReturn = deliveryRepository.save(deliveryInDB);
        messageService.sendMessages(newDelivery.getPerson(), "New Delivery", "Yor delivery was accepted and we will notify you when it's ready !!");
        webSocketService.notifyExternalOrders(dToReturn);
        return dToReturn;
    }

    public Delivery updateDelivery(Delivery delivery) { //only updates delivery guy and person details
        deliveryRepository.findById(delivery.getId()).orElseThrow(() ->
                new ResourceNotFoundException("There is no delivery with the id: " + delivery.getId())
        );
        //  var d = connectPersonToDelivery(delivery);
        Long deliveryGuyId = delivery.getDeliveryGuy() != null ? delivery.getDeliveryGuy().getId() : null;
        deliveryRepository.updateDelivery(deliveryGuyId, delivery.getId());
        webSocketService.notifyExternalOrders(delivery);
        return delivery;
    }

    public void deleteDelivery(Long orderId) {
        Delivery delivery = deliveryRepository.findById(orderId).orElseThrow(() ->
                new ResourceNotFoundException("There is no delivery with the id: " + orderId)
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
            throw new BadRequestException( "The request was in bad format");
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
