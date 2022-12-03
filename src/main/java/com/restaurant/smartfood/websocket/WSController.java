package com.restaurant.smartfood.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class WSController {
    @Autowired
    private WebSocketService service;

    @PostMapping("/api/message")
    public void sendMessage(@RequestBody TextMessageDTO message) {
        service.notifyFrontend(message);
    }

//    @PostMapping("/send-private-message/{id}")
//    public void sendPrivateMessage(@PathVariable final String id,
//                                   @RequestBody final Message message) {
//        service.notifyUser(id, message.getMessageContent());
//    }
}
