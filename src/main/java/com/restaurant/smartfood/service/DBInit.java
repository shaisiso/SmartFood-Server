package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.repostitory.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DBInit implements CommandLineRunner {
    @Autowired
    private MenuItemRepository itemRepository;

    @Override
    public void run(String... args) throws Exception {

        itemRepository.deleteAll();

        MenuItem carpaccio =  MenuItem.builder()
                .name("Beef Carpaccio")
                .price((float) 80)
                .description("Traditional Italian appetizer consisting of raw beef sliced paper-thin, drizzled with olive oil and lemon juice, and finished with capers and onions")
                .category("Starters")
                .build();
        MenuItem wings =  MenuItem.builder()
                .name("Chicken Wings")
                .price((float) 55)
                .description("Chicken wings with chilli")
                .category("Starters")
                .build();
        MenuItem mushrooms =  MenuItem.builder()
                .name("Stir Fried Mushrooms")
                .price((float) 45)
                .description("Stir-Fried Asian Mushroom")
                .category("Starters")
                .build();

        MenuItem lettuceSalad =  MenuItem.builder()
                .name("Lettuce Salad")
                .price((float) 48)
                .description("Lettuce, zucchini, purple onion, cranberries, roasted almonds, sunflower seeds, vinaigrette sauce")
                .category("Salads")
                .build();
        MenuItem greekSalad =  MenuItem.builder()
                .name("Greek Salad")
                .description("Tomatoes, cucumbers, peppers, radishes, purple onions, mint, Bulgarian cheese, and hyssop. Season with olive oil and lemon")
                .price((float) 48)
                .category("Salads")
                .build();
        MenuItem matbucha  =  MenuItem.builder()
                .name("Matbucha")
                .price((float) 45)
                .description("cooked salad consisting of cooked tomatoes and roasted bell peppers seasoned with garlic and chili pepper, and slow-cooked for a number of hours")
                .category("Salads")
                .build();

        MenuItem meatMix =  MenuItem.builder()
                .name("Double Meat Mix")
                .price((float)260)
                .description("200g sinta, 200g entrecote, chicken, kebab, veal sausages")
                .category("Main Dishes")
                .build();
        MenuItem burger =  MenuItem.builder()
                .name("Burger")
                .price((float) 82)
                .description("250g")
                .category("Main Dishes")
                .build();
        MenuItem filet =  MenuItem.builder()
                .name("Filet Mignon")
                .price((float) 166)
                .description("250g")
                .category("Main Dishes")
                .build();

        MenuItem tomhawk =  MenuItem.builder()
                .name("Tomahawk")
                .price((float) 500)
                .description("1200g of Tomahawk steak the with bone")
                .category("Main Dishes")
                .build();

        itemRepository.saveAll(Arrays.asList(carpaccio,wings,mushrooms,greekSalad,
                lettuceSalad,matbucha,meatMix,burger,filet,tomhawk));
    }

}