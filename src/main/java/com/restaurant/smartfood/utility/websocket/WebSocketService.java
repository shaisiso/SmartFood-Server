package com.restaurant.smartfood.utility.websocket;

import com.restaurant.smartfood.entities.CancelItemRequest;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.entities.Shift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate  ) {
        this.messagingTemplate = messagingTemplate;
    }
    public void notifyExternalOrders(Order order){
        messagingTemplate.convertAndSend("/topic/external-orders", order);
    }
    public void notifyMemberOrder(Order order){
        if (order.getPerson() !=null)
            messagingTemplate.convertAndSend("/topic/external-orders/"+order.getPerson().getId(), order);
    }
    public void notifyCancelItemRequest(CancelItemRequest request){
        messagingTemplate.convertAndSend("/topic/cancel-item-requests", request);
    }
    public void notifyShiftsChange(Shift shift){
        messagingTemplate.convertAndSend("/topic/shift",shift);
    }


}
