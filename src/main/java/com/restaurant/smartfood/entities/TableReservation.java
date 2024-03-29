package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
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
public class TableReservation implements Comparable<TableReservation> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_seq")
    @SequenceGenerator(name = "reservation_seq")
    private Long reservationId;

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RestaurantTable table;

    @NotNull(message = "Date must not be null")
    @Column(nullable = false)
    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @NotNull(message = "Hour must not be null")
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime hour;

    @NotNull(message = "Number of diners must not be null")
    @Column(nullable = false)
    @Min(1)
    @Max(value = 15, message = "For more that 15 people, call us")
    private Integer numberOfDiners;

    @Size(max = 255)
    private String additionalDetails;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "person_id", nullable = false)
    @NotNull(message = "Person details must not be null")
    @Valid
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person person;


    @Override
    public int compareTo(TableReservation otherReservation) {
        if (this.getDate().compareTo(otherReservation.getDate()) == 0) // equals date
            return this.getHour().compareTo(otherReservation.getHour());
        return this.getDate().compareTo(otherReservation.getDate());
    }
}