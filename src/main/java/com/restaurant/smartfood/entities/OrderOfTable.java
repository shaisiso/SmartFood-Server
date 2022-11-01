package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
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
    private Boolean isBusy;

    @NotNull
    @Min(1)
    private Integer numberOfDiners;
}