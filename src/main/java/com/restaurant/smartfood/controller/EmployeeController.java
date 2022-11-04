package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{phoneNumber}")
    public Employee getEmployeeByPhoneNumber(@PathVariable("phoneNumber") String employeePhoneNumber) {
        return employeeService.getEmployeeByPhoneNumber(employeePhoneNumber);
    }
    @GetMapping
    public List<Employee> getAllEmployees(){
        return  employeeService.getAllEmployees();
    }
}
