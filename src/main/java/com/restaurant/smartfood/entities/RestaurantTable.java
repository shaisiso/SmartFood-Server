package com.restaurant.smartfood.entities;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "restaurant_tables")
@OnDelete(action = OnDeleteAction.CASCADE)
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tables_seq")
    @SequenceGenerator(name ="tables_seq", sequenceName = "tables_seq", allocationSize = 1,initialValue = 10)
    @Column( nullable = false)
    private Integer tableId;

    @NotNull
    @Column(nullable = false)
    @Min(1)
    private Integer numberOfSeats;
}