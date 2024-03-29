package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.repostitory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

@Service
public class DBInit implements CommandLineRunner {

    private final MenuItemRepository itemRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final PersonRepository personRepository;
    private final EmployeeRepository employeeRepository;
    private final ItemInOrderRepository itemInOrderRepository;
    private final TableReservationRepository tableReservationRepository;
    private final MemberRepository memberRepository;
    private final OrderOfTableRepository orderOfTableRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final TakeAwayRepository takeAwayRepository;
    private final WaitingListRepository waitingListRepository;
    private final DiscountRepository discountRepository;
    private final CancelItemRequestRepository cancelItemRequestRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${timezone.name}")
    private String timezone;
    @Autowired
    public DBInit(MenuItemRepository itemRepository, RestaurantTableRepository restaurantTableRepository, PersonRepository personRepository, EmployeeRepository employeeRepository, ItemInOrderRepository itemInOrderRepository, TableReservationRepository tableReservationRepository, MemberRepository memberRepository, OrderOfTableRepository orderOfTableRepository, OrderRepository orderRepository, DeliveryRepository deliveryRepository, TakeAwayRepository takeAwayRepository, WaitingListRepository waitingListRepository, DiscountRepository discountRepository, CancelItemRequestRepository cancelItemRequestRepository, PasswordEncoder passwordEncoder) {
        this.itemRepository = itemRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.personRepository = personRepository;
        this.employeeRepository = employeeRepository;
        this.itemInOrderRepository = itemInOrderRepository;
        this.tableReservationRepository = tableReservationRepository;
        this.memberRepository = memberRepository;
        this.orderOfTableRepository = orderOfTableRepository;
        this.orderRepository = orderRepository;
        this.deliveryRepository = deliveryRepository;
        this.takeAwayRepository = takeAwayRepository;
        this.waitingListRepository = waitingListRepository;
        this.discountRepository = discountRepository;
        this.cancelItemRequestRepository = cancelItemRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

//        addItemsToMenu();
//        createTables();
 //       addEmployees();
//        addMember();
//        addDiscounts();

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
                .numberOfSeats(15)
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
                .password(passwordEncoder.encode("123456"))
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
                .password(passwordEncoder.encode("123456"))
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
                .password(passwordEncoder.encode("123456"))
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
                .password(passwordEncoder.encode("123456"))
                .role(EmployeeRole.SHIFT_MANAGER)
                .build();

        Employee waiter = Employee.builder()
                .name("Eyal Meshumar")
                .email("em27@gmail.com")
                .address(Address.builder()
                        .city("Kiryat Ata")
                        .streetName("Rambam")
                        .houseNumber(27)
                        .build())
                .phoneNumber("0527272727")
                .password(passwordEncoder.encode("123456"))
                .role(EmployeeRole.WAITER)
                .build();
        Employee hostess = Employee.builder()
                .name("Mishel Gerzig")
                .email("mg1@gmail.com")
                .address(Address.builder()
                        .city("Madrid")
                        .streetName("Santiago Bernabeu")
                        .houseNumber(1)
                        .build())
                .phoneNumber("0500000001")
                .password(passwordEncoder.encode("123456"))
                .role(EmployeeRole.HOSTESS)
                .build();
        Employee kitchenManager = Employee.builder()
                .name("Tjaronn Cherry")
                .email("cherry10@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .streetName("Hacarmel")
                        .houseNumber(10)
                        .build())
                .phoneNumber("0510101010")
                .password(passwordEncoder.encode("123456"))
                .role(EmployeeRole.KITCHEN_MANAGER)
                .build();
        Employee barman  = Employee.builder()
                .name("Din David")
                .email("dinda@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .streetName("Hacarmel")
                        .houseNumber(21)
                        .build())
                .phoneNumber("0521212121")
                .password(passwordEncoder.encode("123456"))
                .role(EmployeeRole.BAR)
                .build();
        Employee barManager = Employee.builder()
                .name("Neta Lavi")
                .email("netal6@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .streetName("Hacarmel")
                        .houseNumber(6)
                        .build())
                .phoneNumber("0506060606")
                .password(passwordEncoder.encode("123456"))
                .role(EmployeeRole.BAR_MANAGER)
                .build();
        Employee kitchen = Employee.builder()
                .name("Lior Refaelov")
                .email("liorr@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .streetName("Hacarmel")
                        .houseNumber(26)
                        .build())
                .phoneNumber("0526262626")
                .password(passwordEncoder.encode("123456"))
                .role(EmployeeRole.BAR_MANAGER)
                .build();
        employeeRepository.saveAll(Arrays.asList(deliveryGuy1, deliveryGuy2, manager, shiftManager,waiter,hostess,kitchenManager,
                barManager,barman,kitchen));
    }

    private void addTableReservation() {
        tableReservationRepository.deleteAll();

        Person p = Person.builder()
                .name("Avi Ben-Shabat")
                .phoneNumber("0523535353")
                .email("aviBen@gmail.com")
                .address(Address.builder()
                        .city("Haifa")
                        .houseNumber(2)
                        .streetName("Dekel")
                        .build())
                .build();
        Person person = personRepository.save(p);
        int hour = LocalTime.now(ZoneId.of(timezone)).plusHours(1).getHour();
        TableReservation t1 = TableReservation.builder().
                table(restaurantTableRepository.findById(11).get())
                .hour(LocalTime.of(hour,0))
                .date(LocalDate.now(ZoneId.of(timezone)))
                .person(person)
                .numberOfDiners(2)
                .build();

        TableReservation t2 = TableReservation.builder().
                table(restaurantTableRepository.findById(11).get())
                .hour(LocalTime.of(12,0))
                .date(LocalDate.now(ZoneId.of(timezone)))
                .person(person)
                .numberOfDiners(2)
                .build();

        tableReservationRepository.saveAll(Arrays.asList(t1, t2));
    }

    private void addOrders() {
        List<Delivery> deliveries = new ArrayList<>();
        List<TakeAway> takeAwayList = new ArrayList<>();
        List<OrderOfTable> orderOfTableList = new ArrayList<>();
        List<RestaurantTable> restaurantTables = restaurantTableRepository.findAll();
        List<MenuItem> menuItems = itemRepository.findAll();
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.now(ZoneId.of(timezone));
        LocalDate dateOfOrder = startDate;
        while (dateOfOrder.compareTo(endDate) <= 0) {
            MenuItem item = menuItems.get((int) (Math.random() * menuItems.size()));
            double ordersNum = Math.random() * 10;
            for (int i = 0; i < ordersNum; i++) {
                // Delivery
                Delivery d = Delivery.builder()
                        .deliveryGuy(employeeRepository.findByPhoneNumber("0588888881").get())
                        .hour(LocalTime.now(ZoneId.of(timezone)))
                        .person(personRepository.findById(1000L).get())
                        .date(dateOfOrder)
                        .originalTotalPrice(item.getPrice())
                        .totalPriceToPay(item.getPrice())
                        .status(OrderStatus.CLOSED)
                        .alreadyPaid(item.getPrice())
                        .build();
                Delivery newDelivery = deliveryRepository.save(d);
                ItemInOrder itemInDelivery = ItemInOrder.buildFromItem(newDelivery, item);
                itemInOrderRepository.save(itemInDelivery);
                newDelivery.setItems(Arrays.asList(itemInDelivery));
                deliveries.add(newDelivery);
            }
            // TA
            ordersNum = Math.random() * 10;
            for (int i = 0; i < ordersNum; i++) {
                TakeAway ta = TakeAway.builder()
                        .hour(LocalTime.now(ZoneId.of(timezone)))
                        .person(personRepository.findById(1000L).get())
                        .date(dateOfOrder)
                        .originalTotalPrice(item.getPrice())
                        .totalPriceToPay(item.getPrice())
                        .status(OrderStatus.CLOSED)
                        .alreadyPaid(item.getPrice())
                        .build();
                TakeAway newTA = takeAwayRepository.save(ta);
                ItemInOrder itemInTA = ItemInOrder.buildFromItem(newTA, item);
                itemInOrderRepository.save(itemInTA);
                newTA.setItems(Arrays.asList(itemInTA));
                takeAwayList.add(newTA);
            }
            // Orders of tables
            ordersNum = Math.random() * 15;
            for (int i = 0; i < ordersNum; i++) {
                RestaurantTable table = restaurantTables.get((int) (Math.random() * restaurantTables.size()));
                OrderOfTable orderOfTable = OrderOfTable.builder()
                        .hour(LocalTime.now(ZoneId.of(timezone)))
                        .date(dateOfOrder)
                        .table(table)
                        .numberOfDiners(table.getNumberOfSeats())
                        .originalTotalPrice(item.getPrice())
                        .totalPriceToPay(item.getPrice())
                        .status(OrderStatus.CLOSED)
                        .alreadyPaid(item.getPrice())
                        .build();
                OrderOfTable newOrderOfTable = orderOfTableRepository.save(orderOfTable);
                ItemInOrder itemInOrder = ItemInOrder.buildFromItem(newOrderOfTable, item);
                itemInOrderRepository.save(itemInOrder);
                newOrderOfTable.setItems(Arrays.asList(itemInOrder));
                orderOfTableList.add(newOrderOfTable);
            }

            dateOfOrder = dateOfOrder.plusDays(1);
        }
        takeAwayRepository.saveAll(takeAwayList);
        deliveryRepository.saveAll(deliveries);
        orderOfTableRepository.saveAll(orderOfTableList);
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
                .password(passwordEncoder.encode("123456"))
                .build();
        memberRepository.saveAll(Arrays.asList(member));
    }

    private void addWaitingList() {
        WaitingList w = WaitingList.builder()
                .date(LocalDate.now(ZoneId.of(timezone)))
                .numberOfDiners(4)
                .hour(LocalTime.of(20, 0))
                .person(memberRepository.findAll().get(0))
                .build();
        waitingListRepository.save(w);
    }

    private void addDiscounts() {
        DayOfWeek dayOfWeek = LocalDate.now(ZoneId.of(timezone)).getDayOfWeek();
        Discount d = Discount.builder()
                .startDate(LocalDate.now(ZoneId.of(timezone)))
                .endDate(LocalDate.of(2023, 11, 30))
                .days(new TreeSet<>(Arrays.asList(dayOfWeek)))
                .categories(Arrays.asList(ItemCategory.STARTERS))
                .startHour(LocalTime.of(13, 30))
                .endHour(LocalTime.of(22, 0))
                .forMembersOnly(false)
                .percent(20)
                .ifYouOrder(2)
                .youGetDiscountFor(1)
                .discountDescription("20% on the 3rd item from the Starters at every " + dayOfWeek.toString())
                .build();
        Discount membersDiscount = Discount.builder()
                .startDate(LocalDate.now(ZoneId.of(timezone)))
                .endDate(LocalDate.of(2023, 11, 30))
                .days(new TreeSet<>(Arrays.asList(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)))
                .categories(ItemCategory.getAllCategories())
                .startHour(LocalTime.of(9, 0))
                .endHour(LocalTime.of(23, 59))
                .forMembersOnly(true)
                .percent(5)
                .ifYouOrder(0)
                .youGetDiscountFor(1)
                .discountDescription("5% on all of the menu for members")
                .build();
        discountRepository.saveAll(Arrays.asList(d, membersDiscount));
    }
}