package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeRole;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.PersonRepository;
import com.restaurant.smartfood.security.ChangePasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@Slf4j
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private  DeliveryService deliveryService;
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
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee with this phone is existed: " + employee.getPhoneNumber());
                    });
                    employeeRepository.insertEmployee(personFromDB.getId(),
                            passwordEncoder.encode(employee.getPassword()), employee.getRole().toString());
                },
                // person is NOT in DB -> save new employee
                () -> {
                    personService.validateFields(employee);
                    employee.setPassword(passwordEncoder.encode(employee.getPassword()));
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
                    employeeRepository.updateEmployee(updatedEmployee.getId(),
                            employeeDB.getPassword()  , updatedEmployee.getRole().toString());
                },
                () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no member with this member id: " + updatedEmployee.getId());
                }
        );
        return updatedEmployee;
    }
    public Employee updatePassword(ChangePasswordRequest changePasswordRequest){
        var employee = getEmployeeByID(changePasswordRequest.getUserId());
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), employee.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password is wrong.");
        var newEncryptedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        employee.setPassword(newEncryptedPassword);
        return employeeRepository.save(employee);
    }
    public Employee getEmployeeByPhoneNumber(String employeePhoneNumber) {
        return employeeRepository.findByPhoneNumber(employeePhoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no employee with phone number: " + employeePhoneNumber));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public void deleteEmployee(Long employeeId) {
        employeeRepository.findById(employeeId)
                .ifPresentOrElse(e -> {
                            onDeleteDeliveryGuySetNull(e);
                            employeeRepository.delete(e);
                        },
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "There is no employee with id: " + employeeId);
                        });
    }

    private void onDeleteDeliveryGuySetNull(Employee employee) {
        if (employee.getRole().equals(EmployeeRole.DELIVERY_GUY)){
            var deliveries = deliveryService.getDeliveriesByDeliveryGuy(employee.getId());
            deliveries.forEach(d-> d.setDeliveryGuy(null));
            deliveryService.saveAll(deliveries);
        }

    }

    public Employee getEmployeeByID(Long employeeID) {
        return employeeRepository.findById(employeeID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no employee with employeeID: " + employeeID));
    }


}
