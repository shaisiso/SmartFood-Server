package com.restaurant.smartfood.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendTaskNotification() {
        TextMessageDTO message = new TextMessageDTO("New Task", LocalDateTime.now().toString());
        messagingTemplate.convertAndSend("/topic/task", message);
    }
//
//    public void sendPrivateNotification(final String userId) {
//        ResponseMessage message = new ResponseMessage("Private Notification");
//
//        messagingTemplate.convertAndSendToUser(userId,"/topic/private-notifications", message);
//    }
}
