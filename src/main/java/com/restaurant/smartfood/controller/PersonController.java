package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/person")
public class PersonController {
    @Autowired
    private PersonService personService;

    @GetMapping("/{phone}")
    public Person GetPersonDetails(@PathVariable("phone") String phoneNumber){
       return personService.getPersonByPhone(phoneNumber);
    }
}
