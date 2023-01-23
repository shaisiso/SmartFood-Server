package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmployeeServiceTest {
    @Autowired
    private EmployeeService employeeService;
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private PersonService personService;
    @MockBean
    private PersonRepository personRepository;
    private Employee existedEmployee;

    @BeforeEach
    void setUp() {
        existedEmployee = Employee.builder()
                .phoneNumber("0522222222")
                .email("a@g.com")
                .id(1L)
                .name("Dani Cohen")
                .build();
        Mockito.doReturn(Optional.of(existedEmployee)).when(personRepository).findByPhoneNumber(existedEmployee.getPhoneNumber());
        Mockito.doReturn(Optional.of(existedEmployee)).when(employeeRepository).findById(existedEmployee.getId());
        Mockito.doReturn(Optional.empty()).when(employeeRepository).findById(2l);
    }
    @Test
    void addEmployeeTwice() {
        var employee= Employee.builder()
                .phoneNumber("0522222222")
                .email("a@g.com")
                .id(1L)
                .name("Dani Cohen")
                .build();
        var expectedException = ConflictException.class;
        assertThrows(expectedException,()->employeeService.addEmployee(employee));
    }
    @Test
    void updateEmployeeNotFound(){
        var employee= Employee.builder()
                .phoneNumber("0522222222")
                .email("a@g.com")
                .id(2L)
                .name("Dani Cohen")
                .build();
        var expectedException = ResourceNotFoundException.class;
        assertThrows(expectedException,()->employeeService.updateEmployee(employee));
    }
}