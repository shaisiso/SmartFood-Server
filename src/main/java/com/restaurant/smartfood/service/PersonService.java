package com.restaurant.smartfood.service;

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
}
