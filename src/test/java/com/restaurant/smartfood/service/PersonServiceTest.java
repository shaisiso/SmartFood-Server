package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.repostitory.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PersonServiceTest {
    @Autowired
    private PersonService personService;
    @MockBean
    private PersonRepository personRepository;
    private Person existedPerson;

    @BeforeEach
    void setUp() {
        existedPerson = Person.builder()
                .phoneNumber("0522222222")
                .email("a@g.com")
                .id(1L)
                .name("Dani Cohen")
                .build();
    }

    @Test
    public void validatePhoneNumberExisted() {
        Mockito.doReturn(Optional.of(existedPerson)).when(personRepository).findByPhoneNumber(existedPerson.getPhoneNumber());
        var newPerson = Person.builder()
                .id(2l)
                .phoneNumber("0522222222")
                .email("a@g.com")
                .name("Dani Cohen")
                .build();
        var expectedException = ConflictException.class;
        assertThrows(expectedException, () -> personService.validatePhoneNumber(newPerson));
    }

    @Test
    public void validateEmail() {
        Mockito.doReturn(Optional.of(existedPerson)).when(personRepository).findByEmail(existedPerson.getEmail());
        var newPerson = Person.builder()
                .id(2l)
                .phoneNumber("0522222222")
                .email("a@g.com")
                .name("Dani Cohen")
                .build();
        var expectedException = ConflictException.class;
        assertThrows(expectedException, () -> personService.validateEmail(newPerson));
    }
}