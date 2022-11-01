package com.restaurant.smartfood.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Builder
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
}