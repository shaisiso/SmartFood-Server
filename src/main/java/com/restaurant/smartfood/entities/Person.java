package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Polymorphism(type = PolymorphismType.EXPLICIT)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @SequenceGenerator(name = "person_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 10, unique = true)
    @Pattern(regexp = "^[0][5][0-9]{8}",message = "Phone number must be 10 consecutive digits in format: 05xxxxxxxxx")
    @NotBlank
    private String phoneNumber;

    @Column(nullable = false, length=20)
    @Pattern(regexp = "^[a-zA-Z\\s-]{2,}",message = "Name must have at least 2 letters and contain only letters in english.")
    @NotBlank
    private String name;


    @Column(unique = true)
    @Email
    private String email;

    @Embedded
    private Address address;
}