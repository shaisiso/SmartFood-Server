package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @Column(name = "discount_id", nullable = false)
    private Long discountId;

    @NotNull
    @Column(nullable = false)
    private Boolean forMembersOnly;

    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    @ElementCollection
    @NotNull
    @Column(nullable = false)
    private Set<DayOfWeek> days;


    @FutureOrPresent
    @JsonFormat(pattern = "HH:mm")
    @NotNull
    @Column(nullable = false)
    private LocalTime startHour;

    @FutureOrPresent
    @JsonFormat(pattern = "HH:mm")
    @NotNull
    @Column(nullable = false)
    private LocalTime endHour;

    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer ifYouOrder;

    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer youGetDiscountFor;

    @Min(0)
    @Max(100)
    @NotNull
    @Column(nullable = false)
    private Integer percent;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private List<ItemCategory> categories;
}