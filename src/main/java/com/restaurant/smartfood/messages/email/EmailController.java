//package com.restaurant.smartfood.messages.email;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.mail.MessagingException;
//import java.io.UnsupportedEncodingException;
//
//@RestController
//@RequestMapping("/api/email")
//public class EmailController {
//
//    @Autowired
//    private  EmailService emailService;
//
//    @PostMapping
//    public void sendEmail(){
//        emailService.sendSimpleMessage("Shaisisso1@gmail.com","Subject","This email is test");
//    }
//    @PostMapping("/attach")
//    public void sendEmailAttachment() throws MessagingException, UnsupportedEncodingException {
//        emailService.sendEmailWithHtml("Shaisisso1@gmail.com","Subject","This email is test");
//    }
//}
