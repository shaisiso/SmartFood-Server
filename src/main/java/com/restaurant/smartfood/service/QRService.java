package com.restaurant.smartfood.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.exception.UnprocessableEntityException;
import com.restaurant.smartfood.security.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class QRService {

    @Autowired
    private RestaurantTableService tableService;

    public String generateTokenForQR(Integer tableId, Integer daysForExpiration) {
        tableService.getTableById(tableId);
        if (daysForExpiration < 1)
            throw new UnprocessableEntityException("TIme for expiration need to be at least 1 day.");

        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
        // Create JWT Token
        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + daysToMillis(daysForExpiration)))
                .withClaim("tableId", tableId)
                .sign(algorithm);
    }

    private long daysToMillis(int days) {
        return (long) days * 24 * 60 * 60 * 1000;
    }

    public RestaurantTable verifyToken(String token) {
        log.info(token);
        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
        // parse the token and validate it
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .build()
                    .verify(token);
            Integer tableId = decodedJWT.getClaim("tableId").asInt();
            return tableService.getTableById(tableId);
        } catch (SignatureVerificationException e) {
            log.error("Authorization was failed . " + e.getMessage());
            log.error("Token was changed and cannot be trusted");
            throw new BadCredentialsException("This link is invalid.");
        } catch (TokenExpiredException e) {
            log.warn("Token expired");
            throw new BadCredentialsException("This link is expired");
        } catch (Exception e) {
            log.error("Could not verify token for a certain reason");
            log.error(e.toString());
            throw new BadCredentialsException("This link is invalid");

        }
    }
}
