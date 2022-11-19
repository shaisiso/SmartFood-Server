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
@Table(name = "deliveries")
public class Delivery extends Order {

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Employee deliveryGuy;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Person personDetails;
}