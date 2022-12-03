package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.security.LoginAuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegisteredUserService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public Employee employeeLogin(LoginAuthenticationRequest credentials) {
        var unauthorizedEx = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        var employee = employeeRepository.findByPhoneNumber(credentials.getPhoneNumber())
                .orElseThrow(() -> unauthorizedEx);
        if (!passwordEncoder.matches(credentials.getPassword(), employee.getPassword())) {
            throw unauthorizedEx;
        }
        return employee;
    }
}
