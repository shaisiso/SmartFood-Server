//package com.restaurant.smartfood.websocket;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.util.HtmlUtils;
//
//@Controller
//public class MessageController {
//
// //   @Autowired
// //   private NotificationService notificationService;
//
//    @MessageMapping("/sendMessage")
//    @SendTo("/topic/messages")
//    public TextMessageDTO  getMessage( TextMessageDTO  message) throws InterruptedException {
//        Thread.sleep(1000);
//      //  notificationService.sendGlobalNotification();
//        return new TextMessageDTO (HtmlUtils.htmlEscape(message.getMessageContent()));
//    }
//
////    @MessageMapping("/private-message")
////    @SendToUser("/topic/private-messages")
////    public ResponseMessage getPrivateMessage(final Message message,
////                                             final Principal principal) throws InterruptedException {
////        Thread.sleep(1000);
////        notificationService.sendPrivateNotification(principal.getName());
////        return new ResponseMessage(HtmlUtils.htmlEscape(
////                "Sending private message to user " + principal.getName() + ": "
////                        + message.getMessageContent())
////        );
////    }
//}
