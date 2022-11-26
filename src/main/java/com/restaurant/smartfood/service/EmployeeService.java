package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Employee addEmployee(Employee employee) {
        personRepository.findByPhoneNumber(employee.getPhoneNumber()).ifPresentOrElse(
                personFromDB -> {
                    // person is existed in DB
                    if (employee.getEmail() != null && !employee.getEmail().equals(personFromDB.getEmail())) // email updated
                        personService.validateEmail(employee);
                    employee.setId(personFromDB.getId());
                    // validate that was not saved already
                    employeeRepository.findById(employee.getId()).ifPresent((p) -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee id existed");
                    });
                    employeeRepository.insertEmployee(personFromDB.getId(), passwordEncoder.encode(employee.getPassword()), employee.getRole().toString());
                },
                // person is NOT in DB -> save new member
                () -> {
                    personService.validateFields(employee);
                    var employeeDB = employeeRepository.save(employee);
                    employee.setId(employeeDB.getId());
                }
        );
        return employee;
    }

    public Employee updateEmployee(Employee updatedEmployee) {
        employeeRepository.findById(updatedEmployee.getId()).ifPresentOrElse(
                employeeDB -> {
                    personService.updatePerson(updatedEmployee);
                    employeeRepository.updateEmployee(updatedEmployee.getId(), passwordEncoder.encode(updatedEmployee.getPassword()), updatedEmployee.getRole().toString());
                },
                () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no member with this member id: " + updatedEmployee.getId());
                }
        );
        return updatedEmployee;
    }

    public Employee getEmployeeByPhoneNumber(String employeePhoneNumber) {
        return employeeRepository.findByPhoneNumber(employeePhoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no employee with phone number: " + employeePhoneNumber));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
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
