package com.restaurant.smartfood.entities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OrderStatus {
    ACCEPTED ("Accepted"),
    PREPARATION("Preparation"),
    READY("Ready"),
    ON_THE_WAY("On The Way"),
    CLOSED ("Closed");

    private final String name;

    OrderStatus(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

    public static Stream<OrderStatus> stream() {
        return Stream.of(OrderStatus.values());
    }

    public static List<String> getStatusNames() {
        return OrderStatus.stream().
                map(status -> status.toString())
                .collect(Collectors.toList());
    }
}
