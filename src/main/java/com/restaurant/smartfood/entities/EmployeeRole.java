package com.restaurant.smartfood.entities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//need to be Class for spring security
public enum EmployeeRole {
    HOSTESS("Hostess"),
    WAITER("Waiter"),
    KITCHEN("Kitchen"),
    KITCHEN_MANAGER("Kitchen Manager"),
    BAR("Bar"),
    BAR_MANAGER("Bar Manager"),
    DELIVERY_GUY("Delivery Guy"),
    SHIFT_MANAGER("Shift Manager"),
    DELIVERY_MANAGER("Delivery Manager"),
    MANAGER("Manager");

    private final String name;

    EmployeeRole(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

    public static Stream<EmployeeRole> stream() {
        return Stream.of(EmployeeRole.values());
    }

    public static List<String> getRolesNames() {
        return EmployeeRole.stream().
                map(role -> role.toString())
                .collect(Collectors.toList());
    }
}
