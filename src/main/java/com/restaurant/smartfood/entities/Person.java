package com.restaurant.smartfood.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "person")
public class Person {
    @Id
    @Column(nullable = false, length = 10)
    @Pattern(regexp = "^[0][5][0-9]{8}",message = "Phone number must be 10 consecutive digits in format: 05xxxxxxxxx")
    @NotBlank
    private String phoneNumber;

    @Column(nullable = false, length=20)
    @Pattern(regexp = "^[a-zA-Z]{2,}",message = "First name must have only letters in english and at least 2 letters")
    @NotBlank
    private String firstName;

    @Column(nullable = false,length=20)
    @Pattern(regexp = "^[a-zA-Z]{2,}",message = "Last name must have only letters in english and at least 2 letters")
    @NotBlank
    private String lastName;

    @Column(unique = true)
    @Email
    private String email;

    @Embedded
    private Address address;
}