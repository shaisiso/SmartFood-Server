package com.restaurant.smartfood.messages.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

	@Autowired
	@Qualifier("twilio")
  	private SmsSender smsSender;

    public void sendSms(SmsRequest smsRequest) {
        smsSender.sendSms(smsRequest);
    }
}
