package com.restaurant.smartfood.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

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


    @OneToOne(optional = false)
    @JoinColumn(nullable = false, name = "employee_id",referencedColumnName = "id")
    private EmployeeID employeeID;

    @NotBlank
    @Column(nullable = false)
    @Size(min = 6,message = "Password needs to be at least 8 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EmployeeRole role;
}