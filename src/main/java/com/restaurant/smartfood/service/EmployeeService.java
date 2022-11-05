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
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeIDRepository employeeIDRepository;

    @Autowired
    private PersonService personService;

    public Employee saveEmployee(Employee newEmployee) {
        personService.validateFields(newEmployee);
        return employeeRepository.save(newEmployee);
    }

    public Employee getEmployeeByPhoneNumber(String employeePhoneNumber) {
        return employeeRepository.findByPhoneNumber(employeePhoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no employee with phone number: " + employeePhoneNumber));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(Employee updatedEmployee) {
        var employeeFromDB = employeeRepository.findById(updatedEmployee.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no employee with phone number: " + updatedEmployee.getPhoneNumber()));
        if (!updatedEmployee.getPhoneNumber().equals(employeeFromDB.getPhoneNumber())) // phone updated
            personService.validatePhoneNumber(updatedEmployee);
        if (!updatedEmployee.getEmail().equals(employeeFromDB.getEmail())) // email updated
            personService.validateEmail(updatedEmployee);
        return employeeRepository.save(updatedEmployee);
    }

    public void deleteEmployee(Employee employee) {
        employeeRepository.findById(employee.getId())
                .ifPresentOrElse(e -> {
                            employeeRepository.delete(employee);
                        },
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "There is no employee with phone number: " + employee.getPhoneNumber());
                        });
    }

    public Employee getEmployeeByID(Long employeeID) {
        return employeeRepository.findById(employeeID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no employee with employeeID: " + employeeID));
    }
}
