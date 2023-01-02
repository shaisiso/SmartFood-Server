package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Polymorphism;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "waiting_list")
public class WaitingList {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "waiting_list_seq")
    @SequenceGenerator(name = "waiting_list_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Date must not be null")
    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Hour must not be null")
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime hour;

    //    @NotNull
//    @ManyToOne(optional = false)
//    @JoinColumn(nullable = false)
//    private Member member;
    @ManyToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "person_id", nullable = false)
    @NotNull(message = "Person details must not be null")
    @Valid
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private Person person;

    @NotNull(message = "Number of diners must not be null")
    @Column(nullable = false)
    @Min(1)
    @Max(value = 15, message = "For more that 15 people, call us")
    private Integer numberOfDiners;

}