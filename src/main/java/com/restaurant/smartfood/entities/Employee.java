package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true)

public class Employee extends Person {

    @NotNull
    private Long employeeID;

    @NotBlank
    private String password;

    @NotNull
    private String role; // need to change String to Role
}