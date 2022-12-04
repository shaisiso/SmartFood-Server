package com.restaurant.smartfood.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationTokens {
    private String accessToken;
    private String refreshToken;
}
