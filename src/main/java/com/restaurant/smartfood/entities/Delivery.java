package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "deliveries")
public class Delivery extends Order {


    @ManyToOne
    @JoinColumn
    @Valid
    private Employee deliveryGuy;

//    @NotNull
//    @ManyToOne(optional = false)
//    @JoinColumn(nullable = false)
//    @Valid
//    private Person person;
}