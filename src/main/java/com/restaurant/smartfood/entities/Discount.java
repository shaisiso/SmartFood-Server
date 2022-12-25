package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "discount_seq")
    @SequenceGenerator(name = "discount_seq")
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
    @NotEmpty(message = "Days must not be empty")
    @Column(nullable = false)
    private Set<DayOfWeek> days;

    @JsonFormat(pattern = "HH:mm")
    @NotNull
    @Column(nullable = false)
    private LocalTime startHour;

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
    @NotEmpty(message = "Categories must not be empty")
    @Column(nullable = false)
    private List<ItemCategory> categories;

    private String discountDescription;
}