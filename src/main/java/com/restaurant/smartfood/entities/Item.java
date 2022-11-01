package com.restaurant.smartfood.entities;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemID_seq")
    @SequenceGenerator(name = "itemID_seq")
    @Column(name = "itemID", nullable = false)
    private Long itemID;

    private String name;

    private String description;

    private Float price;

    private Category category;
}