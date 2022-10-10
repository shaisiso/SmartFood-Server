package com.restaurant.smartfood.entities;
import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @SequenceGenerator(name ="menu_sequence", sequenceName = "menu_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menu_sequence")
    private Long itemId;

    @NotBlank
    @Column(length = 30)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ItemCategory category;

    private String description;

    @NotNull
    @DecimalMin(value = "0",inclusive = false)
    private Float price;
}