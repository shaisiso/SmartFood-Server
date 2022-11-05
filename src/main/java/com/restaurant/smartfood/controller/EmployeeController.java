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
@Slf4j
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
    @PutMapping("/{phoneNumber}")
    public Employee updateEmployee(@PathVariable("phoneNumber") String employeePhoneNumber,
                                   @Valid @RequestBody Employee updatedEmployee){
        return employeeService.updateEmployee(updatedEmployee, employeePhoneNumber);
    }
    @GetMapping
    public List<Employee> getAllEmployees(){
        return  employeeService.getAllEmployees();
    }
}
