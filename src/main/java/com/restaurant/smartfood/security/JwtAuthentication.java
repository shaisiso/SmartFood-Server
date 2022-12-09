package com.restaurant.smartfood.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired)) //Autowired annotated lombok generated constructor
@Component
public class JwtAuthentication {

    /*
     * After Server validated credentials, this method will create a JWT that will be sent to Client
     */
    public ResponseEntity<AuthorizationTokens> createTokens(UserDetails principal)  {
        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
        // Create JWT Token
        String accessToken;
        accessToken = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("roles", principal.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);

        String refreshToken;
        refreshToken = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * JwtProperties.EXPIRATION_TIME))
                .sign(algorithm);

        var tokens = AuthorizationTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        ResponseEntity<AuthorizationTokens> response = new ResponseEntity<>(tokens, HttpStatus.OK);
        return response;
    }

}
