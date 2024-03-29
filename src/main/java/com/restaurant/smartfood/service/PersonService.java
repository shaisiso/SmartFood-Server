package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public Person savePerson(Person newPerson) {
        if (newPerson.getEmail() != null && newPerson.getEmail().isEmpty())
            newPerson.setEmail(null);
        Optional<Person> optionalPerson = personRepository.findByPhoneNumber(newPerson.getPhoneNumber());
        if (optionalPerson.isPresent()) {
            Person personFromDB = optionalPerson.get();
            if (newPerson.getEmail() != null && !newPerson.getEmail().equals(personFromDB.getEmail())) // email updated
                validateEmail(newPerson);
            newPerson.setId(personFromDB.getId());
            copyPerson(newPerson, personFromDB);
            personRepository.save(personFromDB);
        } else {
            validateFields(newPerson);
            personRepository.save(newPerson);
        }
        return newPerson;
    }

    private void copyPerson(Person from, Person to) {
        to.setName(from.getName());
        to.setEmail(from.getEmail());
        to.setAddress(from.getAddress());
        to.setPhoneNumber(from.getPhoneNumber());
    }

    public Person updatePerson(Person person) {
        if (person.getEmail() != null && person.getEmail().isEmpty())
            person.setEmail(null);
        Optional<Person> optionalPerson = personRepository.findById(person.getId());
        if (optionalPerson.isPresent()) {
            Person personFromDB = optionalPerson.get();
            if (person.getEmail() != null && !person.getEmail().equals(personFromDB.getEmail())) // email updated
                validateEmail(person);
            if (!person.getPhoneNumber().equals(personFromDB.getPhoneNumber()))
                validatePhoneNumber(person);
            setPersonDetails(person, personFromDB);
            personRepository.save(personFromDB);
        } else {
            throw new ResourceNotFoundException("There is no one with id : " + person.getId());
        }

        return person;
    }

    private void setPersonDetails(Person personFrom, Person personTo) {
        personTo.setEmail(personFrom.getEmail());
        personTo.setAddress(personFrom.getAddress());
        personTo.setName(personFrom.getName());
        personTo.setPhoneNumber(personFrom.getPhoneNumber());
    }

    public Person getPersonByPhone(String phoneNumber) {
        return personRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("There is no person with phone number: " + phoneNumber));
    }

    public Optional<Person> getOptionalPersonByPhone(String phoneNumber) {
        return personRepository.findByPhoneNumber(phoneNumber);
    }

    public void validatePhoneNumber(Person person) {
        personRepository.findByPhoneNumber(person.getPhoneNumber())
                .ifPresent(p -> {
                    if (person.getId() != null && !person.getId().equals(p.getId()))
                        throw new ConflictException(p.getName() + " has this phone number: " + person.getPhoneNumber());
                });
    }

    public void validateEmail(Person person) {
        if (person.getEmail() != null) {
            personRepository.findByEmail(person.getEmail())
                    .ifPresent(p -> {
                        if (person.getId() != null && !person.getId().equals(p.getId()))
                            throw new ConflictException(p.getName() + " has this email: " + person.getEmail());
                    });
        }
    }

    public void validateFields(Person person) {
        validatePhoneNumber(person);
        validateEmail(person);
    }
}
