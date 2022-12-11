package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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


    @ManyToOne()
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_delivery_guy_id",
            foreignKeyDefinition = " /*FOREIGN KEY in sql that sets ON DELETE SET NULL*/"))
    private Employee deliveryGuy;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Person personDetails;
}