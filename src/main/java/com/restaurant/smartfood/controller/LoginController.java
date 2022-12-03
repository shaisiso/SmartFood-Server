package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.security.LoginAuthenticationRequest;
import com.restaurant.smartfood.service.EmployeeService;
import com.restaurant.smartfood.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@CrossOrigin
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    private RegisteredUserService registeredUserService;

    @PostMapping
    public Employee employeeLogin(@RequestBody LoginAuthenticationRequest credentials) throws InterruptedException {
        return registeredUserService.employeeLogin(credentials);
    }
}
