package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Cascade;


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
@Getter
@Setter
@ToString
@Entity
@Table(name = "table_reservations")
public class TableReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_seq")
    @SequenceGenerator(name = "reservation_seq")
    private Long reservationId;

    @ManyToOne(optional = false,cascade ={ CascadeType.MERGE,CascadeType.REMOVE} )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private RestaurantTable table;

    @NotNull
    @Column(nullable = false)
    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @NotNull
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:00")
    private LocalTime hour;

    @NotNull
    @Column(nullable = false)
    @Min(1)
    private Integer numberOfDiners;

    @Size(max = 255)
    private String additionalDetails;

    @ManyToOne(optional = false,cascade ={ CascadeType.MERGE,CascadeType.REMOVE})
    @JoinColumn(name = "person_id",nullable = false)
    @NotNull
    @Valid
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private Person person;
}