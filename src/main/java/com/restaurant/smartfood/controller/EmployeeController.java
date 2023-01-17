package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeRole;
import com.restaurant.smartfood.security.AuthorizeEmployee;
import com.restaurant.smartfood.security.AuthorizeGeneralManager;
import com.restaurant.smartfood.security.AuthorizeManagers;
import com.restaurant.smartfood.security.ChangePasswordRequest;
import com.restaurant.smartfood.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@CrossOrigin
@Slf4j
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/roles")
    public List<String> getRoles() {
        return EmployeeRole.getRolesNames();
    }
    @PostMapping
    @AuthorizeGeneralManager
    public Employee addEmployee(@Valid @RequestBody Employee newEmployee) {
        return employeeService.addEmployee(newEmployee);
    }

    @GetMapping("/phone/{phoneNumber}")
    @AuthorizeEmployee
    public Employee getEmployeeByPhoneNumber(@PathVariable("phoneNumber") String employeePhoneNumber) {
        return employeeService.getEmployeeByPhoneNumber(employeePhoneNumber);
    }

    @PutMapping
    @AuthorizeEmployee
    public Employee updateEmployee(@Valid @RequestBody Employee updatedEmployee) {
        return employeeService.updateEmployee(updatedEmployee);
    }

    @PutMapping("/password")
    @AuthorizeEmployee
    public Employee updatePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest){
        return employeeService.updatePassword(changePasswordRequest);
    }

    @DeleteMapping("/{employeeId}")
    @AuthorizeGeneralManager
    public void deleteEmployee(@PathVariable("employeeId") Long employeeId) {
        employeeService.deleteEmployee(employeeId);
    }

    @GetMapping
    @AuthorizeManagers
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
    @GetMapping("/{id}")
    @AuthorizeEmployee
    public Employee getEmployeeByID(@PathVariable("id") Long employeeID) {
        return employeeService.getEmployeeByID(employeeID);
    }

}
