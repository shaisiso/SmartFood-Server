package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_seq")
    @SequenceGenerator(name = "shift_seq")
    private Long shiftID;

    @NotNull
    @ManyToOne
    private Employee employee;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm")
    @Column(nullable = false)
    private LocalDateTime shiftEntrance;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm")
    private LocalDateTime shiftExit;
}