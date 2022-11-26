package com.restaurant.smartfood.security;

import lombok.Data;

@Data
public class LoginAuthenticationRequest {
    private String phoneNumber;
    private String password;
}
