package com.restaurant.smartfood.entities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum ItemCategory {
    //SALADS,
    STARTERS("Starters"),
    MAIN_DISHES("Main Dishes"),
    SIDE_DISHES("Side Dishes"),
    DESERTS("Deserts"),
    COLD_DRINKS("Cold Drinks"),
    HOT_DRINKS("Hot Drinks"),
    ALCOHOL("Alcohol");
    private final String name;

    ItemCategory(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

    public static Stream<ItemCategory> stream() {
        return Stream.of(ItemCategory.values());
    }

    public static List<String> getCategoriesNames() {
        return ItemCategory.stream().
                map(category -> category.toString())
                .collect(Collectors.toList());
    }
}
