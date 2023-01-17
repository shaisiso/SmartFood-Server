package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.security.AuthorizationTokens;
import com.restaurant.smartfood.security.LoginAuthenticationRequest;
import com.restaurant.smartfood.security.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController()
@CrossOrigin
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    private RegisteredUserService registeredUserService;

    @PostMapping("/employee")
    public ResponseEntity<AuthorizationTokens> employeeLogin(@RequestBody LoginAuthenticationRequest credentials)  {
        return registeredUserService.employeeLogin(credentials);
    }
    @PostMapping("/member")
    public ResponseEntity<AuthorizationTokens> memberLogin(@RequestBody LoginAuthenticationRequest credentials)  {
        return registeredUserService.memberLogin(credentials);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthorizationTokens>  refreshToken( @RequestBody AuthorizationTokens tokens)  {
        return registeredUserService.refreshToken(tokens);
    }
}
