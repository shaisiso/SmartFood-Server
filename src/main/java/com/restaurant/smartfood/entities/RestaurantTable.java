package com.restaurant.smartfood.entities;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "restaurant_table")
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tables_sequence")
    @SequenceGenerator(name ="tables_sequence", sequenceName = "tables_sequence", allocationSize = 1,initialValue = 10)
    @Column(name = "id", nullable = false)
    private Integer tableId;

    @NotNull
    @Min(1)
    private Integer numberOfSeats;
}