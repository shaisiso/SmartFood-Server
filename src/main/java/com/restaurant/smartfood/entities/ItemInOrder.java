package com.restaurant.smartfood.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "item_in_order")
public class ItemInOrder {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne
    private Order order;

    @NotNull
    @OneToOne
    private MenuItem item;

    private String itemComment;

    @NotNull
    private Integer quantity;

    @NotNull
    private Float price;
}