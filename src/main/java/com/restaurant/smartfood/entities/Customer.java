package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@SuperBuilder
@AllArgsConstructor
@ToString
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Polymorphism(type = PolymorphismType.EXPLICIT)
@Table(name = "customer")
public class Customer extends Person {
}