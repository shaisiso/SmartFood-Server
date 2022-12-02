package com.restaurant.smartfood.security;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class RegisteredUserPrincipalService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RegisteredUserPrincipal registeredUserPrincipal;
        var employeeOp = employeeRepository.findByPhoneNumber(username);
        if (employeeOp.isPresent())
            registeredUserPrincipal = new RegisteredUserPrincipal(employeeOp.get());
        else{
           var member =  memberRepository.findByPhoneNumber(username).orElseThrow(()->{
                log.error("User not found in the database");
                throw new UsernameNotFoundException("User with this phone number is not registered : " + username);
            });
            registeredUserPrincipal = new RegisteredUserPrincipal(member);
        }
        return registeredUserPrincipal;
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Refresh token is missing");
        }
        try {
            // verify token
            String token = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");
            Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
            String userName = JWT.require(algorithm).build().verify(token).getSubject();

            // generate new access and refresh tokens
            UserDetails principal = this.loadUserByUsername(userName);
            // Create JWT Token
            String accessToken = JWT.create().withSubject(principal.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                    .withIssuer(request.getRequestURL().toString()).withClaim("roles", principal.getAuthorities()
                            .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .sign(algorithm);
            String refreshToken = JWT.create().withSubject(principal.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * JwtProperties.EXPIRATION_TIME))
                    .withIssuer(request.getRequestURL().toString()).sign(algorithm);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (SignatureVerificationException e) { // token was changed
            log.error("Authorization was failed. " + e.getMessage());
            log.error("Token was changed and cannot be trusted");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authorization was failed.");
        } catch (TokenExpiredException e) { // token expired
            log.error(e.toString());
            response.setHeader("Error", e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            Map<String, String> error = new HashMap<>();
            error.put("Error_message", "Refresh token expired, need to re-login");
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } catch (Exception e) {
            JwtAuthorizationFilter.tokenErrorHandler(response, log, e.toString(), e.getMessage(), e);
        }
    }

}

