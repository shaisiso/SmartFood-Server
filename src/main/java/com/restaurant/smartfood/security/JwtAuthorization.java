package com.restaurant.smartfood.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class JwtAuthorization {


    public void authorizeRequest(HttpServletRequest request, String[] requestedAuthorities) {

        // Read the Authorization header, where the JWT token should be
        String header = request.getHeader(JwtProperties.HEADER_STRING);


        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            log.warn("Authorization token is missing");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized for this action");
        }
        try {
            DecodedJWT decodedJWT = verifyToken(request);
            verifyPermission(decodedJWT, requestedAuthorities);
        } catch (SignatureVerificationException e) {
            log.error("Authorization was failed. " + e.getMessage());
            log.error("Token was changed and cannot be trusted");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization was failed.");
        } catch (TokenExpiredException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is expired");
        } catch (Exception e) {
            if (e instanceof ResponseStatusException)
                throw e;
            else {
                log.error("The authorization failed for a certain reason");
                log.error(e.toString());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized for this action.");
            }
        }
    }
    public DecodedJWT verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
        // parse the token and validate it
        DecodedJWT decodedJWT = JWT.require(algorithm)
                .build()
                .verify(token);
        return decodedJWT;
    }
    public DecodedJWT verifyToken(HttpServletRequest request) {
        String token = request.getHeader(JwtProperties.HEADER_STRING)
                .replace(JwtProperties.TOKEN_PREFIX, "");
        return verifyToken(token);
    }

    private void verifyPermission(DecodedJWT decodedJWT, String[] requestedAuthorities) {
        //get roles
        List<String> roles = Arrays.stream(decodedJWT.getClaim("roles").asArray(String.class)).collect(Collectors.toList());

        AtomicBoolean hasPermission = new AtomicBoolean(false);
        Stream.of(requestedAuthorities).forEach(requestedAuthority -> {
            if (roles.contains(requestedAuthority)) {
               // log.debug("contain");
                hasPermission.set(true);
                return;
            }
        });
        if (hasPermission.get() == false)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have the permission for this request");
    }
}
