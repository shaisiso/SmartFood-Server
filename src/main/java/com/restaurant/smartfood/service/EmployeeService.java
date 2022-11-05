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
        newEmployee.setEmployeeID(employeeIDRepository.save(new EmployeeID()));
        return employeeRepository.save(newEmployee);
    }

    public Employee getEmployeeByPhoneNumber(String employeePhoneNumber) {
        return employeeRepository.findByPhoneNumber(employeePhoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no employee with phone number: " + employeePhoneNumber));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(Employee updatedEmployee, String phoneNumber) {
        var employeeFromDB = employeeRepository.findByPhoneNumber(phoneNumber).get();
        if (!updatedEmployee.getPhoneNumber().equals(phoneNumber)) // phone updated
            personService.validatePhoneNumber(updatedEmployee);

        if (!updatedEmployee.getEmail().equals(employeeFromDB.getEmail())) // email updated
            personService.validateEmail(updatedEmployee);
        employeeRepository.delete(employeeFromDB);
        return employeeRepository.save(updatedEmployee);
    }
    public void deleteEmployee(Employee employee) {
        employeeRepository.findByPhoneNumber(employee.getPhoneNumber())
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
