package com.restaurant.smartfood.entities;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "table_reservation")
public class TableReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_sequence")
    @SequenceGenerator(name = "reservation_sequence")
    private Long id;

    @ManyToOne
    private RestaurantTable table;

    @NotNull
    @FutureOrPresent
    private LocalDate date;

    @NotNull
    private LocalTime hour;

    @NotNull
    private Integer numberOfDiners;

    @Length(max = 255)
    private String additionalDetails;
}