package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "table_reservation")
public class TableReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_sequence")
    @SequenceGenerator(name = "reservation_sequence")
    private Long id;

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

    @Length(max = 255)
    private String additionalDetails;
}