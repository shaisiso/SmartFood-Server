package com.restaurant.smartfood.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

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
    public static LocalTime hourPlusDurationForReservation(LocalTime hour,int durationForReservation){
        var hourPlus = hour.plusHours(durationForReservation);
        if (hourPlus.compareTo(hour) <= 0) //passed 00:00
            hourPlus = LocalTime.of(23, 59);
        return hourPlus;
    }

}
