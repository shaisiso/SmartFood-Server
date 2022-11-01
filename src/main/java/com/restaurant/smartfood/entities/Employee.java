package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "employees")
public class Employee extends Person {

    @NotNull
    @Column(nullable = false,unique = true)
    @SequenceGenerator(name ="employee_id_sequence", sequenceName = "employee_id_sequence", allocationSize = 1,initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_sequence")
    private Long employeeID;

    @NotBlank
    @Column(nullable = false)
    @Size(min = 8,message = "Password needs to be at least 8 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotBlank
    private EmployeeRole role;
}