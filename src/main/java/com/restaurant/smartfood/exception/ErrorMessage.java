package com.restaurant.smartfood.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {

    private int status;
    private HttpStatus error;
    private String message;
    private String description;

}