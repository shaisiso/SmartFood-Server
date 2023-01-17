package com.restaurant.smartfood.messages;

import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.messages.email.EmailService;
import com.restaurant.smartfood.messages.sms.SmsRequest;
import com.restaurant.smartfood.messages.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MessageService {
    private final SmsService smsService;
    private final EmailService emailService;
    @Value("${message-service.send-emails}")
    private boolean emailsSendEnabled;
    @Value("${message-service.default-mail}")
    private String defaultMail;

    @Autowired
    public MessageService(SmsService smsService, EmailService emailService) {
        this.smsService = smsService;
        this.emailService = emailService;
    }

    @Async
    public void sendMessages(Person person, String subject, String message) {
        if (person == null) {
            log.warn("Person details are missing, could not send message");
            return;
        }
        SmsRequest sms = SmsRequest.builder()
                .phoneNumber(person.getPhoneNumber())
                .message(message)
                .build();
        smsService.sendSms(sms);
        if (!emailsSendEnabled)
            emailService.sendEmail(defaultMail, subject, message);
        else if (person.getEmail() != null && !person.getEmail().isEmpty())
            emailService.sendEmail(person.getEmail(), subject, message);
    }
}
