package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee newEmployee) {
        return employeeRepository.save(newEmployee);
    }

    public Employee getEmployeeByPhoneNumber(String employeePhoneNumber) {
        return employeeRepository.findById(employeePhoneNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
