package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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

    @NotNull
    @Min(1)
    private Integer numberOfDiners;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(nullable = false)
    private RestaurantTable table;
}