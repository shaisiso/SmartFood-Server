package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.service.EmployeeService;
import com.restaurant.smartfood.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController()
@CrossOrigin
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public Employee AddEmployee(@Valid @RequestBody Employee newEmployee) {
        return employeeService.saveEmployee(newEmployee);
    }


    @GetMapping("/{phoneNumber}")
    public Employee GetEmployeeByPhoneNumber(@PathVariable("phoneNumber") String employeePhoneNumber) {
        return employeeService.getEmployeeByPhoneNumber(employeePhoneNumber);
    }
}
