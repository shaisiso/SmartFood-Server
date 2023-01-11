package com.restaurant.smartfood.messages.sms;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("twilio")
@Data
@NoArgsConstructor
@ToString
public class TwilioConfiguration {

    private String accountSid;
    private String authToken;
    private String trialNumber;
    private String phoneNumberTo;
}
