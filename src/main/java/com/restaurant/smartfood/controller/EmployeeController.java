package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Employee;
//import com.restaurant.smartfood.security.PreAuthorizeGeneralManager;
//import com.restaurant.smartfood.security.PreAuthorizeManagers;
//import com.restaurant.smartfood.security.PreAuthorizeMember;
import com.restaurant.smartfood.security.Authorize;
import com.restaurant.smartfood.security.AuthorizeManagers;
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

    @PostMapping
    public Employee addEmployee(@Valid @RequestBody Employee newEmployee) {
        return employeeService.addEmployee(newEmployee);
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
    //@Authorize(roles = {"ROLE_MANAGER","ROLE_SHIFT_MANAGER"})
    @AuthorizeManagers
    public List<Employee> getAllEmployees() {

        log.debug("controller");
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeByID(@PathVariable("id") Long employeeID) {
        return employeeService.getEmployeeByID(employeeID);
    }

}
