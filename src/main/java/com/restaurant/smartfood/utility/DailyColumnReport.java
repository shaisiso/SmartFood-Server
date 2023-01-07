package com.restaurant.smartfood.utility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyColumnReport {

    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private Double value;
}
