package com.restaurant.smartfood.utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static LocalDate parseToLocalDate(String date){
        return  LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    public static LocalTime parseToLocalTime(String time){
        return  LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
    }
}
