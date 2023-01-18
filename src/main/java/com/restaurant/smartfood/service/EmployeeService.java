package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Delivery;
import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeRole;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.PersonRepository;
import com.restaurant.smartfood.security.ChangePasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    private DeliveryService deliveryService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Employee addEmployee(Employee employee) {
        Optional<Person> optionalPerson = personRepository.findByPhoneNumber(employee.getPhoneNumber());
        if (optionalPerson.isPresent()) {            // person is existed in DB
            Person personFromDB = optionalPerson.get();
            if (employee.getEmail() != null && !employee.getEmail().equals(personFromDB.getEmail())) // email updated
                personService.validateEmail(employee);
            employee.setId(personFromDB.getId());
            // validate that was not saved already
            employeeRepository.findById(employee.getId()).ifPresent((p) -> {
                throw new ConflictException( "Employee with this phone is existed: " + employee.getPhoneNumber());
            });
            employeeRepository.insertEmployee(personFromDB.getId(),
                    passwordEncoder.encode(employee.getPassword()), employee.getRole().toString());
        } else {        // person is NOT in DB -> save new employee
            personService.validateFields(employee);
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            Employee employeeDB = employeeRepository.save(employee);
            employee.setId(employeeDB.getId());
        }
        return employee;
    }

    public Employee updateEmployee(Employee updatedEmployee) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(updatedEmployee.getId());
        if (optionalEmployee.isPresent()) {
            Employee employeeDB = optionalEmployee.get();
            personService.updatePerson(updatedEmployee);
            employeeRepository.updateEmployee(updatedEmployee.getId(),
                    employeeDB.getPassword(), updatedEmployee.getRole().toString());
        } else {
            throw new ResourceNotFoundException( "There is no member with this member id: " + updatedEmployee.getId());
        }
        return updatedEmployee;
    }

    public Employee updatePassword(ChangePasswordRequest changePasswordRequest) {
        Employee employee = getEmployeeByID(changePasswordRequest.getUserId());
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), employee.getPassword()))
            throw new BadCredentialsException( "Old password is wrong.");
        String newEncryptedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        employee.setPassword(newEncryptedPassword);
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeByPhoneNumber(String employeePhoneNumber) {
        return employeeRepository.findByPhoneNumber(employeePhoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException( "There is no employee with phone number: " + employeePhoneNumber));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public void deleteEmployee(Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee e = optionalEmployee.get();
            onDeleteDeliveryGuySetNull(e);
            employeeRepository.delete(e);
        } else {
            throw new ResourceNotFoundException(  "There is no employee with id: " + employeeId);
        }
    }

    private void onDeleteDeliveryGuySetNull(Employee employee) {
        if (employee.getRole().equals(EmployeeRole.DELIVERY_GUY)) {
            List<Delivery> deliveries = deliveryService.getDeliveriesByDeliveryGuy(employee.getId());
            deliveries.forEach(d -> d.setDeliveryGuy(null));
            deliveryService.saveAll(deliveries);
        }

    }

    public Employee getEmployeeByID(Long employeeID) {
        return employeeRepository.findById(employeeID)
                .orElseThrow(() -> new ResourceNotFoundException( "There is no employee with employeeID: " + employeeID));
    }


}
