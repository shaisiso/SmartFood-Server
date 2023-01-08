package com.restaurant.smartfood.utility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyColumnReport<T> {
    private Month month;
    private T value;
}
