package com.restaurant.smartfood.websocket;

import com.restaurant.smartfood.entities.Delivery;
import com.restaurant.smartfood.entities.Shift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate  , NotificationService notificationService ) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }
    public void notifyNewDelivery(Delivery delivery){
      //  notificationService.sendTaskNotification();
        messagingTemplate.convertAndSend("/topic/task", delivery);
    }
    public void notifyNewShiftEntrance(Shift shift){
        messagingTemplate.convertAndSend("/topic/shift",shift);
    }

    public void notifyFrontend(TextMessageDTO message) {
        TextMessageDTO  response =  TextMessageDTO.builder()
                .date(message.getDate()).message(message.getMessage())
                .build();
      //  notificationService.sendGlobalNotification();
        messagingTemplate.convertAndSend("/topic/chat", response);
    }

//    public void notifyUser(final String id, final String message) {
//        ResponseMessage response = new ResponseMessage(message);
//
//        notificationService.sendPrivateNotification(id);
//        messagingTemplate.convertAndSendToUser(id, "/topic/private-messages", response);
//    }
}
