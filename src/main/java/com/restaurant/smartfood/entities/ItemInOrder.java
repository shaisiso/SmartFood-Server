package com.restaurant.smartfood.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "items_in_orders")
public class ItemInOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_in_orders_seq")
    @SequenceGenerator(name = "items_in_orders_seq")
    private Long id;

    @NotNull
    @ManyToOne
    private Order order;

    @NotNull
    @ManyToOne
    private MenuItem item;

    private String itemComment;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    @Min(0)
    private Float price;
}