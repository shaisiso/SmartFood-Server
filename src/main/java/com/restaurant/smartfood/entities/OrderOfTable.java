package com.restaurant.smartfood.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "order_of_table")
public class OrderOfTable extends Order {

    private Boolean isBusy;

    private Integer numberOfDiners;
}