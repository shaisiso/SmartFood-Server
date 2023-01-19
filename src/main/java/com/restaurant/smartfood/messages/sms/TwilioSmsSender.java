package com.restaurant.smartfood.messages.sms;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("twilio")
@Slf4j
public class TwilioSmsSender implements SmsSender {


    private final TwilioConfiguration twilioConfiguration;

    @Autowired
    public TwilioSmsSender(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
    }

    @Override
    public void sendSms(SmsRequest smsRequest) {
        if (!smsRequest.getPhoneNumber().startsWith("+972"))
            smsRequest.setPhoneNumber("+972" + smsRequest.getPhoneNumber());
        String header = "SmartFood:";

        PhoneNumber to = new PhoneNumber(twilioConfiguration.getPhoneNumberTo());//new PhoneNumber(smsRequest.getPhoneNumber());
        PhoneNumber from = new PhoneNumber(twilioConfiguration.getTrialNumber());
        String message = "*** " + header + "\n" + smsRequest.getMessage();
        try {
            MessageCreator creator = Message.creator(to, from, message);
            //.setMediaUrl("https://www.twilio.com/blog/sms-spring-boot-app");
            Message response = creator.create();

            log.info("Send sms: {}", response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            log.warn("Could not send SMS ");
        }

    }


}
