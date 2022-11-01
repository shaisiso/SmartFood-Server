package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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
    @Column(nullable = false,unique = true)
    @SequenceGenerator(name ="employee_id_sequence", sequenceName = "employee_id_sequence", allocationSize = 1,initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_sequence")
    private Long employeeID;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotBlank
    private EmployeeRole role;
}