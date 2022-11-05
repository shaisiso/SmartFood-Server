package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Customer;
import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeID;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.repostitory.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PersonService personService;

    //TODO: finish add Reservation(failed with postman)
    public Customer saveCustomer(Customer newCustomer) {
        customerRepository.findByPhoneNumber(newCustomer.getPhoneNumber()).ifPresentOrElse(
                customerFromDB->{
                    if (newCustomer.getEmail()!=null && !newCustomer.getEmail().equals(customerFromDB.getEmail())) // email updated
                        personService.validateEmail(newCustomer);
                    newCustomer.setId(customerFromDB.getId());
                },
                ()->{
                    personService.validateFields(newCustomer);
                }
        );
        return customerRepository.save(newCustomer);
    }
}
