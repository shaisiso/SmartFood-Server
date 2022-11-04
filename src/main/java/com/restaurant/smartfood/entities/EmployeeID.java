package com.restaurant.smartfood.entities;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.*;

@Entity
@Table(name = "employee_id")
@ToString
public class EmployeeID {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_seq")
    @SequenceGenerator(name = "employee_id_seq",initialValue = 1000,allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

//    @OneToOne
//
//    private Employee employee;
}