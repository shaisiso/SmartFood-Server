package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "orders_of_table")
public class OrderOfTable extends Order {

    @NotNull(message = "Number of diners must not be null")
    @Min(1)
    private Integer numberOfDiners;

    @ManyToOne(optional = false)
    @NotNull(message = "Table must not be null")
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RestaurantTable table;

}