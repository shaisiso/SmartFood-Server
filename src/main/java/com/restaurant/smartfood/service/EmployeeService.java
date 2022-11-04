package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeID;
import com.restaurant.smartfood.repostitory.EmployeeIDRepository;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeIDRepository employeeIDRepository;

    public Employee saveEmployee(Employee newEmployee) {
        newEmployee.setEmployeeID(employeeIDRepository.save(new EmployeeID()));
        return employeeRepository.save(newEmployee);
    }

    public Employee getEmployeeByPhoneNumber(String employeePhoneNumber) {
        return employeeRepository.findById(employeePhoneNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no employee with phone number: "+employeePhoneNumber));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
