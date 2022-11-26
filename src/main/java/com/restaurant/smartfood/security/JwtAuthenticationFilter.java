package com.restaurant.smartfood.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /*
     * Client Send credentials and Server validate it
     * Trigger when we issue POST request to /login
     * We also need to pass in {"phoneNumber":"phone", "password":"pass"} in the request body
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            // Grab credentials and map them to LoginAuthenticationRequest
            LoginAuthenticationRequest credentials = new ObjectMapper().
                    readValue(request.getInputStream(), LoginAuthenticationRequest.class);
            log.info("Attempt to login with phone number : "+ credentials.getPhoneNumber());

            // Create login token
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    credentials.getPhoneNumber(),
                    credentials.getPassword());

            // Authenticate user
            Authentication auth = authenticationManager.authenticate(authenticationToken);

            return auth;


        } catch (IOException e) {
            log.error("Error logging in: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    /*
     * After Server validated credentials, this method will create a JWT that will be sent to Client
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // Grab principal
        RegisteredUserPrincipal principal = (RegisteredUserPrincipal) authResult.getPrincipal();

        //Algorithm.HMAC256(JwtProperties.SECRET.getBytes());
        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());

        // Create JWT Token
        String accessToken;
        accessToken = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                //Specify who created and signed the token
                .withIssuer(request.getRequestURL().toString())
                //Pass the roles to token
                .withClaim("roles",principal.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);

        String refreshToken;
        refreshToken = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10*JwtProperties.EXPIRATION_TIME ))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);


        // Add token in response -- no refresh token
        // response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX +  accessToken);

        //set header with access and refres tokens

//        response.setHeader("access_token", accessToken);
//        response.setHeader("refresh_token", refreshToken);

        Map<String,String> tokens=new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

    }
}
