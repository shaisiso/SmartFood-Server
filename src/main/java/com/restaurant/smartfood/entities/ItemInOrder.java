package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
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
@JsonIdentityInfo(property = "id",generator = ObjectIdGenerators.PropertyGenerator.class)
public class ItemInOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_in_orders_seq")
    @SequenceGenerator(name = "items_in_orders_seq")
    private Long id;

    @ManyToOne(optional = false)
    private Order order;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(nullable = false)
    private MenuItem item;

    private String itemComment;

    @Column(nullable = false)
    @DecimalMin(value = "0")
    private Float price;
}