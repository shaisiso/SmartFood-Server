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
        validateFields(newEmployee);
        newEmployee.setEmployeeID(employeeIDRepository.save(new EmployeeID()));
        return employeeRepository.save(newEmployee);
    }

    public Employee getEmployeeByPhoneNumber(String employeePhoneNumber) {
        return employeeRepository.findById(employeePhoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no employee with phone number: " + employeePhoneNumber));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(Employee updatedEmployee, String phoneNumber) {
        var employeeFromDB = employeeRepository.findById(phoneNumber).get();
        if (!updatedEmployee.getPhoneNumber().equals(phoneNumber)) // phone updated
            validatePhoneNumber(updatedEmployee);

        if (!updatedEmployee.getEmail().equals(employeeFromDB.getEmail())) // email updated
            validateEmail(updatedEmployee);
        employeeRepository.delete(employeeFromDB);
        return employeeRepository.save(updatedEmployee);
    }

    private void validatePhoneNumber(Employee employee) {
        employeeRepository.findById(employee.getPhoneNumber())
                .ifPresent(e -> {
                    throw new ResponseStatusException
                            (HttpStatus.CONFLICT, "The employee " + e.getName() + " has this phone number.");
                });
    }

    private void validateEmail(Employee employee) {
        employeeRepository.findByEmail(employee.getEmail())
                .ifPresent(e -> {
                    throw new ResponseStatusException
                            (HttpStatus.CONFLICT, "The employee " + e.getName() + " has this email.");
                });
    }

    private void validateFields(Employee employee) {
        validatePhoneNumber(employee);
        validateEmail(employee);
    }

    public void deleteEmployee(Employee employee) {
        employeeRepository.findById(employee.getPhoneNumber())
                .ifPresentOrElse(e -> {
                            employeeRepository.delete(employee);
                        },
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "There is no employee with phone number: " + employee.getPhoneNumber());
                        });
    }

    public Employee getEmployeeByEmployeeID(Long employeeID) {
        return employeeRepository.findByEmployeeID_Id(employeeID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no employee with employeeID: " + employeeID));
    }
}
