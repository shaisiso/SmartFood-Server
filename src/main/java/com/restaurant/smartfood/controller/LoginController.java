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

    @PostMapping
    public ResponseEntity<AuthorizationTokens> employeeLogin(@RequestBody LoginAuthenticationRequest credentials) throws InterruptedException {
        return registeredUserService.login(credentials);
    }
}
