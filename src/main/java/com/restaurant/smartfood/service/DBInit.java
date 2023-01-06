package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.repostitory.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DBInit implements CommandLineRunner {

    private final MenuItemRepository itemRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final PersonRepository personRepository;
    private final EmployeeRepository employeeRepository;
    private final ItemInOrderRepository itemInOrderRepository;
    private final TableReservationRepository tableReservationRepository;
    private final MemberRepository memberRepository;

    private final DeliveryRepository deliveryRepository;
    private final WaitingListRepository waitingListRepository;
    private final DiscountRepository discountRepository;
    private final CancelItemRequestRepository cancelItemRequestRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args)  {
        tableReservationRepository.deleteAll();
        employeeRepository.deleteAll();
        personRepository.deleteAll();
        cancelItemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        restaurantTableRepository.deleteAll();


        addItemsToMenu();
        createTables();
        addTableReservation();
        addEmployees();
        addOrder();
        addMember();
        addWaitingList();
        addDelivery();
        addDiscounts();
    }

    private void addItemsToMenu() {
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
        RestaurantTable t1 = RestaurantTable.builder()
                .numberOfSeats(2)
                .isBusy(false)
                .build();
        RestaurantTable t2 = RestaurantTable.builder()
                .numberOfSeats(2)
                .isBusy(false)
                .build();
        RestaurantTable t3 = RestaurantTable.builder()
                .numberOfSeats(2)
                .isBusy(false)
                .build();
        RestaurantTable t4 = RestaurantTable.builder()
                .numberOfSeats(4)
                .isBusy(false)
                .build();
        RestaurantTable t5 = RestaurantTable.builder()
                .numberOfSeats(4)
                .isBusy(false)
                .build();
        RestaurantTable t6 = RestaurantTable.builder()
                .numberOfSeats(6)
                .isBusy(false)
                .build();
        RestaurantTable t7 = RestaurantTable.builder()
                .numberOfSeats(6)
                .isBusy(false)
                .build();
        RestaurantTable t8 = RestaurantTable.builder()
                .numberOfSeats(8)
                .isBusy(false)
                .build();
        RestaurantTable t9 = RestaurantTable.builder()
                .numberOfSeats(8)
                .isBusy(false)
                .build();
        RestaurantTable t10 = RestaurantTable.builder()
                .numberOfSeats(20)
                .isBusy(false)
                .build();
        RestaurantTable t11 = RestaurantTable.builder()
                .numberOfSeats(4)
                .isBusy(false)
                .build();
        restaurantTableRepository.saveAll(Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11));
    }

    private void addEmployees() {
        Employee deliveryGuy1 = Employee.builder()
                .name("Dolev Haziza")
                .email("Haziza@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .streetName("Horev")
                        .houseNumber(8)
                        .build())
                .phoneNumber("0588888881")
                .password(passwordEncoder.encode("123456") )
                .role(EmployeeRole.DELIVERY_GUY)
                .build();
        Employee deliveryGuy2 = Employee.builder()
                .name("Sun Menachem")
                .email("sunm@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .streetName("Horev")
                        .houseNumber(12)
                        .build())
                .phoneNumber("0588888882")
                .password(passwordEncoder.encode("123456") )
                .role(EmployeeRole.DELIVERY_GUY)
                .build();
        Employee manager = Employee.builder()
                .name("Barak Bachar")
                .email("bb@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .streetName("Sami Offer")
                        .houseNumber(10)
                        .build())
                .phoneNumber("0523213400")
                .password(passwordEncoder.encode("123456") )
                .role(EmployeeRole.MANAGER)
                .build();

        Employee shiftManager = Employee.builder()
                .name("Yaniv Katan")
                .email("yk20@gmail.com")
                .address(Address.builder()
                        .city("Kiryat Ata")
                        .streetName("Pol Gogen")
                        .houseNumber(20)
                        .build())
                .phoneNumber("0520202020")
                .password(passwordEncoder.encode("123456") )
                .role(EmployeeRole.SHIFT_MANAGER)
                .build();

        employeeRepository.saveAll(Arrays.asList(deliveryGuy1,deliveryGuy2,manager,shiftManager));
    }

    private void addTableReservation() {
        tableReservationRepository.deleteAll();

        var p2 = Person.builder()
                .name("Cristiano Ronaldo")
                .phoneNumber("0577777777")
                .address(Address.builder()
                        .city("Portugal")
                        .houseNumber(7)
                        .streetName("Midiera")
                        .build())
                .build();
        personRepository.save(p2);

        var p = Person.builder()
                .name("Avi Ben-Shabat")
                .phoneNumber("0523535353")
                .email("aviBen@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .houseNumber(2)
                        .streetName("Dekel")
                        .build())
                .build();
        TableReservation t = TableReservation.builder().
                table(restaurantTableRepository.findById(11).get())
                .hour(LocalTime.now())
                .date(LocalDate.now())
                .person(personRepository.save(p))
                .numberOfDiners(2)
                .build();
        tableReservationRepository.saveAll(Arrays.asList(t));
    }

    private void addOrder() {
//        orderRepository.deleteAll();
//        var o = Order.builder()
//                .date(LocalDate.now())
//                .hour(LocalTime.now())
//                .originalTotalPrice(itemRepository.findById((long)1).get().getPrice())
//                .totalPriceToPay(itemRepository.findById((long)1).get().getPrice())
//                .status(OrderStatus.ACCEPTED)
//                .alreadyPaid((float)0)
//                .build();
//        var newOrder = orderRepository.save(o);
//
//        var i = ItemInOrder.builder()
//                .order(newOrder)
//                .item(itemRepository.findById((long)1).get())
//                .price(itemRepository.findById((long)1).get().getPrice())
//                .build();
//        itemInOrderRepository.save(i);
//        newOrder.setItems(Arrays.asList(i));
//        orderRepository.save(newOrder);
    }
    private void addMember() {
        Member member = Member.builder()
                .name("Frank Lampard")
                .email("Franky@gmail.com")
                .address(Address.builder()
                        .city("London")
                        .streetName("Stamford")
                        .houseNumber(8)
                        .build())
                .phoneNumber("0521234567")
                .password(passwordEncoder.encode("123456") )
                .build();
        memberRepository.saveAll(Arrays.asList(member));
    }

    private void addWaitingList() {
        WaitingList w = WaitingList.builder()
                .date(LocalDate.now())
                .numberOfDiners(4)
                .hour(LocalTime.of(20,00))
                .person(memberRepository.findAll().get(0))
                .build();
        waitingListRepository.save(w);
    }
    private void addDelivery() {
        Delivery d = Delivery.builder()
                .deliveryGuy(employeeRepository.findByPhoneNumber("0588888881").get())
                .hour(LocalTime.now())
                .person(personRepository.findByPhoneNumber("0521234567").get())
                .date(LocalDate.now())
                .originalTotalPrice(itemRepository.findAll().get(0).getPrice())
                .totalPriceToPay(itemRepository.findAll().get(0).getPrice())
                .status(OrderStatus.ACCEPTED)
                .alreadyPaid((float)0)
                .build();
        var newDelivery = deliveryRepository.save(d);

        var i = ItemInOrder.builder()
                .order(newDelivery)
                .item(itemRepository.findAll().get(0))
                .price(itemRepository.findAll().get(0).getPrice())
                .build();
        itemInOrderRepository.save(i);
        newDelivery.setItems(Arrays.asList(i));

        deliveryRepository.save(newDelivery);
    }

    private void addDiscounts() {
        var dayOfWeek =LocalDate.now().getDayOfWeek();
        var d = Discount.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.of(2023,11,30))
                .days(new TreeSet<>(Arrays.asList(dayOfWeek)))
                .categories(Arrays.asList(ItemCategory.STARTERS))
                .startHour(LocalTime.of(13,30))
                .endHour(LocalTime.of(22,00))
                .forMembersOnly(false)
                .percent(20)
                .ifYouOrder(2)
                .youGetDiscountFor(1)
                .discountDescription("20% on the 3rd item from the Starters at every "+dayOfWeek.toString())
                .build();
        var membersDiscount = Discount.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.of(2023,11,30))
                .days(new TreeSet<>(Arrays.asList(DayOfWeek.SUNDAY,DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,DayOfWeek.THURSDAY,DayOfWeek.FRIDAY,DayOfWeek.SATURDAY) ))
                .categories(ItemCategory.getAllCategories())
                .startHour(LocalTime.of(9,00))
                .endHour(LocalTime.of(23,59))
                .forMembersOnly(true)
                .percent(5)
                .ifYouOrder(0)
                .youGetDiscountFor(1)
                .discountDescription("5% on all of the menu for members")
                .build();
        discountRepository.saveAll(Arrays.asList(d,membersDiscount));
    }
}