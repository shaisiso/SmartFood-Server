package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.repostitory.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public Person savePerson(Person newPerson) {
        personRepository.findByPhoneNumber(newPerson.getPhoneNumber()).ifPresentOrElse(
                personFromDB -> {
                    if (newPerson.getEmail() != null && !newPerson.getEmail().equals(personFromDB.getEmail())) // email updated
                        validateEmail(newPerson);
                    newPerson.setId(personFromDB.getId());
                },
                () -> {
                    validateFields(newPerson);
                }
        );
        return personRepository.save(newPerson);
    }

    public Person updatePerson(Person person) {
        personRepository.findById(person.getId()).ifPresentOrElse(
                personFromDB -> {
                    if (person.getEmail() != null && !person.getEmail().equals(personFromDB.getEmail())) // email updated
                        validateEmail(person);
                    if (!person.getPhoneNumber().equals(personFromDB.getPhoneNumber()))
                        validatePhoneNumber(person);
                    setPersonDetails(person,personFromDB);
                    personRepository.save(personFromDB);
                },
                () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no one with id : " + person.getId());
                }
        );
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no person with phone number: " + phoneNumber));
    }

    public Optional<Person> getOptionalPersonByPhone(String phoneNumber) {
        return personRepository.findByPhoneNumber(phoneNumber);
    }

    public void deletePerson(Person person) {
        personRepository.delete(person);
    } //TODO: maybe not needed

    public void validatePhoneNumber(Person person) {
        personRepository.findByPhoneNumber(person.getPhoneNumber())
                .ifPresent(p -> {
                    if (person.getId() != null && person.getId().equals(p.getId()))
                        throw new ResponseStatusException
                                (HttpStatus.CONFLICT, p.getName() + " has this phone number: " + person.getPhoneNumber());
                });
    }

    public void validateEmail(Person person) {
        if (person.getEmail() != null) {
            personRepository.findByEmail(person.getEmail())
                    .ifPresent(p -> {
                        if (person.getId() != null && person.getId().equals(p.getId()))
                            throw new ResponseStatusException
                                    (HttpStatus.CONFLICT, p.getName() + " has this email: " + person.getEmail());
                    });
        }
    }

    public void validateFields(Person person) {
        validatePhoneNumber(person);
        validateEmail(person);
    }
}
