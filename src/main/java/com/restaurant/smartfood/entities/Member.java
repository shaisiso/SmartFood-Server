package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SuperBuilder
@AllArgsConstructor
//@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "members")
public class Member extends RegisteredUser { // TODO: add extends RegisteredUser (also for employee)

//    @NotBlank
//    @Column(nullable = false)
//    @Size(min = 6,message = "Password needs to be at least 8 characters")
//    private String password;
}