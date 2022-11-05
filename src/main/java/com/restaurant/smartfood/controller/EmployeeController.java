package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Path;
import javax.validation.Valid;
import java.util.List;

@RestController()
@CrossOrigin
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public Employee addEmployee(@Valid @RequestBody Employee newEmployee) {
        return employeeService.saveEmployee(newEmployee);
    }

    @GetMapping("/phone/{phoneNumber}")
    public Employee getEmployeeByPhoneNumber(@PathVariable("phoneNumber") String employeePhoneNumber) {
        return employeeService.getEmployeeByPhoneNumber(employeePhoneNumber);
    }

    @PutMapping
    public Employee updateEmployee(@Valid @RequestBody Employee updatedEmployee) {
        return employeeService.updateEmployee(updatedEmployee);
    }

    @DeleteMapping
    public void deleteEmployee(@RequestBody Employee employee) {
        employeeService.deleteEmployee(employee);
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{employeeID}")
    public Employee getEmployeeByEmployeeID(@PathVariable("employeeID") Long employeeID) {
        return employeeService.getEmployeeByEmployeeID(employeeID);
    }

}
