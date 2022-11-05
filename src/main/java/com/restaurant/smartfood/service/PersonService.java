package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.repostitory.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public Person getPersonByPhone(String phoneNumber) {
        return personRepository.findById(phoneNumber)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no person with phone number: "+phoneNumber));
    }
    public void validatePhoneNumber(Person person) {
        personRepository.findById(person.getPhoneNumber())
                .ifPresent(p -> {
                    throw new ResponseStatusException
                            (HttpStatus.CONFLICT, p.getName() + " has this phone number.");
                });
    }

    public void validateEmail(Person person) {
        personRepository.findByEmail(person.getEmail())
                .ifPresent(p -> {
                    throw new ResponseStatusException
                            (HttpStatus.CONFLICT, p.getName() + " has this email.");
                });
    }

    public void validateFields(Person person) {
        validatePhoneNumber(person);
        validateEmail(person);
    }
}
