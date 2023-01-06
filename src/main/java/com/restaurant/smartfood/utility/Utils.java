package com.restaurant.smartfood.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static LocalDate parseToLocalDate(String date){
        return  LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
