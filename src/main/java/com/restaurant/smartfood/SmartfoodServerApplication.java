package com.restaurant.smartfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SmartfoodServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartfoodServerApplication.class, args);
    }

}
