package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;


import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString
@Entity
@Table(name = "table_reservation")
public class TableReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_sequence")
    @SequenceGenerator(name = "reservation_sequence")
    private Long reservationId;

    @ManyToOne()
    private RestaurantTable table;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate date;

    @NotNull
    @JsonFormat(pattern = "HH:00")
    private LocalTime hour;

    @NotNull
    @Min(1)
    private Integer numberOfDiners;

    @Size(max = 255)
    private String additionalDetails;

    @ManyToOne
    @JoinColumn(name = "person_phone_number")
    @NotNull
    @Valid
    private Person person;
}