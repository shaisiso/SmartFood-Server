package com.restaurant.smartfood.entities;
import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "menu_items")
@OnDelete(action = OnDeleteAction.CASCADE)
public class MenuItem {

    @Id
    @SequenceGenerator(name ="menu_sequence", sequenceName = "menu_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menu_sequence")
    private Long itemId;

    @NotBlank
    @Column(length = 30,nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20,nullable = false)
    private ItemCategory category;

    private String description;

    @NotNull
    @Column(nullable = false)
    @DecimalMin(value = "0",inclusive = false)
    private Float price;
}