package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "delivery")
public class Delivery extends Order {

    @NotNull
    @ManyToOne
    private Employee deliveryGuy;

    private String comment;

    @ManyToOne
   // @JoinColumn(name = "customer_phone_number")
    private Customer customer;
}