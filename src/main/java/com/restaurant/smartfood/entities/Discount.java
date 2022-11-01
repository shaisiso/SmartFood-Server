package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "discount")
public class Discount {
    @Id
    @Column(name = "discountID", nullable = false)
    private Long discountID;

    private Boolean forMembersOnly;

    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @ElementCollection
    private Set<DayOfWeek> days;

    @FutureOrPresent
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startHour;

    @FutureOrPresent
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endHour;

    @Min(1)
    private Integer ifYouOrder;

    @Min(1)
    private Integer youGetDiscountFor;

    @Min(0)
    @Max(100)
    private Integer percent;
}