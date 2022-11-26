package com.restaurant.smartfood.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_HOSTESS') or " +
        "hasRole('ROLE_WAITER') or " +
        "hasRole('ROLE_KITCHEN') or " +
        "hasRole('ROLE_KITCHEN_MANAGER') or " +
        "hasRole('ROLE_BAR') or " +
        "hasRole('ROLE_BAR_MANAGER') or" +
        " hasRole('ROLE_DELIVERY_GUY') or" +
        " hasRole('ROLE_SHIFT_MANAGER') or" +
        " hasRole('ROLE_DELIVERY_MANAGER') or " +
        "hasRole('ROLE_MANAGER')")
public @interface PreAuthorizeEmployee {
}
