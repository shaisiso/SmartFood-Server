package com.restaurant.smartfood.security;


import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public  @interface AuthorizeManagers {
}
