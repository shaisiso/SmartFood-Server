package com.restaurant.smartfood.messages.sms;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
			smsRequest.setPhoneNumber("+972"+smsRequest.getPhoneNumber());
		String header = "SmartFood:";
		String url = getUrl();
		
        PhoneNumber to =  new PhoneNumber(twilioConfiguration.getPhoneNumberTo());//new PhoneNumber(smsRequest.getPhoneNumber());
        PhoneNumber from = new PhoneNumber(twilioConfiguration.getTrialNumber());
        String message ="*** "+ header + "\n" + smsRequest.getMessage();
        try{
			MessageCreator creator = Message.creator(to, from, message);
			//.setMediaUrl("https://www.twilio.com/blog/sms-spring-boot-app");
			Message response =creator.create();

			log.info("Send sms: {}",response.getBody());
		}catch (Exception e){
			log.error(e.getMessage());
		}

    }

	private String getUrl() {
		 InetAddress ip;
		 String clientPort = "3000";
	     String ipAddress,hostname;
	        try {
	            ip = InetAddress.getLocalHost();
	            hostname = ip.getHostName();
	            ipAddress=ip.getHostAddress();
	            log.info("Your current IP address : " + ipAddress);
	            log.info("Your current Hostname : " + hostname);
	            ipAddress+=":" + clientPort;
	        } catch (UnknownHostException e) {
	            e.printStackTrace();
	            ipAddress ="";
	        }
	        return ipAddress;
	}

}
