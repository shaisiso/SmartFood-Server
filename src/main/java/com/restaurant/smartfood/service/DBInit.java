package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.repostitory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

@Service
public class DBInit implements CommandLineRunner {
    @Autowired
    private MenuItemRepository itemRepository;
    @Autowired
    private RestaurantTableRepository restaurantTableRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeIDRepository employeeIDRepository;

    @Autowired
    private TableReservationRepository tableReservationRepository;

    @Override
    public void run(String... args) throws Exception {
        addItemsToMenu();
        createTables();
        addTableReservation();
        addEmployee();
    }

    private void addItemsToMenu() {
        itemRepository.deleteAll();

        MenuItem carpaccio = MenuItem.builder()
                .name("Beef Carpaccio")
                .price((float) 80)
                .description("Traditional Italian appetizer consisting of raw beef sliced paper-thin, drizzled with olive oil and lemon juice, and finished with capers and onions")
                .category(ItemCategory.STARTERS)
                .build();
        MenuItem wings = MenuItem.builder()
                .name("Chicken Wings")
                .price((float) 55)
                .description("Chicken wings with chilli")
                .category(ItemCategory.STARTERS)
                .build();
        MenuItem mushrooms = MenuItem.builder()
                .name("Stir Fried Mushrooms")
                .price((float) 45)
                .description("Stir-Fried Asian Mushroom")
                .category(ItemCategory.STARTERS)
                .build();

        MenuItem lettuceSalad = MenuItem.builder()
                .name("Lettuce Salad")
                .price((float) 48)
                .description("Lettuce, zucchini, purple onion, cranberries, roasted almonds, sunflower seeds, vinaigrette sauce")
                .category(ItemCategory.STARTERS) //SALADS
                .build();
        MenuItem greekSalad = MenuItem.builder()
                .name("Greek Salad")
                .description("Tomatoes, cucumbers, peppers, radishes, purple onions, mint, Bulgarian cheese, and hyssop. Season with olive oil and lemon")
                .price((float) 48)
                .category(ItemCategory.STARTERS) //SALADS
                .build();
        MenuItem matbucha = MenuItem.builder()
                .name("Matbucha")
                .price((float) 45)
                .description("cooked salad consisting of cooked tomatoes and roasted bell peppers seasoned with garlic and chili pepper, and slow-cooked for a number of hours")
                .category(ItemCategory.STARTERS) //SALADS
                .build();

        MenuItem meatMix = MenuItem.builder()
                .name("Double Meat Mix")
                .price((float) 260)
                .description("200g sinta, 200g entrecote, chicken, kebab, veal sausages")
                .category(ItemCategory.MAIN_DISHES)
                .build();
        MenuItem burger = MenuItem.builder()
                .name("Burger")
                .price((float) 82)
                .description("250g")
                .category(ItemCategory.MAIN_DISHES)
                .build();
        MenuItem filet = MenuItem.builder()
                .name("Filet Mignon")
                .price((float) 166)
                .description("250g")
                .category(ItemCategory.MAIN_DISHES)
                .build();
        MenuItem tomahawk = MenuItem.builder()
                .name("Tomahawk")
                .price((float) 500)
                .description("1200g of Tomahawk steak the with bone")
                .category(ItemCategory.MAIN_DISHES)
                .build();

        MenuItem chips = MenuItem.builder()
                .name("French Fries")
                .price((float) 35)
                .description("Hot and crispy french fries. served with different sauces")
                .category(ItemCategory.SIDE_DISHES)
                .build();

        MenuItem rice = MenuItem.builder()
                .name("Asian rice")
                .price((float) 25)
                .description("Simple asian rice")
                .category(ItemCategory.SIDE_DISHES)
                .build();

        MenuItem brulee = MenuItem.builder()
                .name("Creme Brulee")
                .price((float) 30)
                .description("Rich custard base topped with a layer of hardened caramelized sugar. Served slightly chilled")
                .category(ItemCategory.DESERTS)
                .build();
        MenuItem tiramisu = MenuItem.builder()
                .name("Tiramisu")
                .price((float) 38)
                .description("Layers of biscotti with mascarpone cream, chocolate chips, espresso and Amaretto liqueur")
                .category(ItemCategory.DESERTS)
                .build();

        MenuItem cola = MenuItem.builder()
                .name("Coca Cola")
                .price((float) 15)
                .category(ItemCategory.COLD_DRINKS)
                .build();
        MenuItem zero = MenuItem.builder()
                .name("Coca Cola Zero")
                .price((float) 15)
                .category(ItemCategory.COLD_DRINKS)
                .build();
        MenuItem sprite = MenuItem.builder()
                .name("Sprite")
                .price((float) 15)
                .category(ItemCategory.COLD_DRINKS)
                .build();
        MenuItem grapeJuice = MenuItem.builder()
                .name("Grape Juice")
                .price((float) 15)
                .category(ItemCategory.COLD_DRINKS)
                .build();
        MenuItem fuzeTea = MenuItem.builder()
                .name("Fuze Tea - Peach")
                .price((float) 15)
                .category(ItemCategory.COLD_DRINKS)
                .build();
        MenuItem water = MenuItem.builder()
                .name("Mineral Water")
                .price((float) 12)
                .category(ItemCategory.COLD_DRINKS)
                .build();
        MenuItem soda = MenuItem.builder()
                .name("Soda")
                .price((float) 10)
                .category(ItemCategory.COLD_DRINKS)
                .build();
        MenuItem lemonGarus = MenuItem.builder()
                .name("Grated Minted Lemonade")
                .price((float) 19)
                .category(ItemCategory.COLD_DRINKS)
                .build();

        MenuItem shortEspresso = MenuItem.builder()
                .name("Short Espresso")
                .price((float) 10)
                .category(ItemCategory.HOT_DRINKS)
                .build();
        MenuItem longEspresso = MenuItem.builder()
                .name("Long Espresso")
                .price((float) 10)
                .category(ItemCategory.HOT_DRINKS)
                .build();
        MenuItem doubleEspresso = MenuItem.builder()
                .name("Double Espresso")
                .price((float) 12)
                .category(ItemCategory.HOT_DRINKS)
                .build();
        MenuItem macchiato = MenuItem.builder()
                .name("Macchiato")
                .price((float) 11)
                .category(ItemCategory.HOT_DRINKS)
                .build();
        MenuItem cappuccino = MenuItem.builder()
                .name("Cappuccino")
                .price((float) 15)
                .category(ItemCategory.HOT_DRINKS)
                .build();

        MenuItem carlsberg = MenuItem.builder()
                .name("Carlsberg")
                .price((float) 32)
                .description("1/2 Liter of Carlsberg beer from the barrel")
                .category(ItemCategory.ALCOHOL)
                .build();
        MenuItem tuborg = MenuItem.builder()
                .name("Tuborg")
                .price((float) 32)
                .description("1/2 Liter of Tuborg beer from the barrel")
                .category(ItemCategory.ALCOHOL)
                .build();
        MenuItem weihenstephan = MenuItem.builder()
                .name("Weihenstephan")
                .price((float) 37)
                .description("1/2 Liter of Weihenstephan beer from the barrel")
                .category(ItemCategory.ALCOHOL)
                .build();
        MenuItem beluga = MenuItem.builder()
                .name("Beluga vodka chaiser")
                .price((float) 53)
                .description("Beluga vodka chaiser")
                .category(ItemCategory.ALCOHOL)
                .build();

        itemRepository.saveAll(Arrays.asList(carpaccio, wings, mushrooms, greekSalad, lettuceSalad, matbucha,
                meatMix, burger, filet, tomahawk,
                chips, rice,
                brulee, tiramisu,
                cola, zero, sprite, grapeJuice, fuzeTea, water, soda, lemonGarus,
                shortEspresso, longEspresso, doubleEspresso, macchiato, cappuccino,
                carlsberg, tuborg, weihenstephan, beluga));
    }

    private void createTables() {
        restaurantTableRepository.deleteAll();
        RestaurantTable t1 = RestaurantTable.builder()
                .numberOfSeats(2)
                .build();
        RestaurantTable t2 = RestaurantTable.builder()
                .numberOfSeats(2)
                .build();
        RestaurantTable t3 = RestaurantTable.builder()
                .numberOfSeats(2)
                .build();
        RestaurantTable t4 = RestaurantTable.builder()
                .numberOfSeats(4)
                .build();
        RestaurantTable t5 = RestaurantTable.builder()
                .numberOfSeats(4)
                .build();
        RestaurantTable t6 = RestaurantTable.builder()
                .numberOfSeats(6)
                .build();
        RestaurantTable t7 = RestaurantTable.builder()
                .numberOfSeats(6)
                .build();
        RestaurantTable t8 = RestaurantTable.builder()
                .numberOfSeats(8)
                .build();
        RestaurantTable t9 = RestaurantTable.builder()
                .numberOfSeats(8)
                .build();
        RestaurantTable t10 = RestaurantTable.builder()
                .numberOfSeats(20)
                .build();
        RestaurantTable t11 = RestaurantTable.builder()
                .numberOfSeats(4)
                .build();
        restaurantTableRepository.saveAll(Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11));
    }

    private void addEmployee() {
        employeeRepository.deleteAll();

        Employee employee1 = Employee.builder()
                .name("Dolev Haziza")
                .email("Haziza@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .streetName("Horev")
                        .houseNumber(8)
                        .build())
                .phoneNumber("0588888881")
                .password("123456")
                .role(EmployeeRole.BAR)
                .build();
        employeeRepository.saveAll(Arrays.asList(employee1));
    }

    private void addTableReservation() {
        tableReservationRepository.deleteAll();
        var p = Person.builder()
                .name("Avi Ben-Shabat")
                .phoneNumber("0523535353")
                .address(Address.builder()
                        .city("Haifa")
                        .houseNumber(2)
                        .streetName("Dekel")
                        .build())
                .build();
        TableReservation t = TableReservation.builder().
                table(restaurantTableRepository.findById(10).get())
                .hour(LocalTime.now())
                .date(LocalDate.now())
                .person(personRepository.save(p))
                .numberOfDiners(4)
        .build();
        tableReservationRepository.saveAll(Arrays.asList(t));
    }
}