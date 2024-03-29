package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.OrderRepository;
import com.restaurant.smartfood.utility.Utils;
import com.restaurant.smartfood.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemInOrderService itemInOrderService;
    private final MemberService memberService;
    private final DiscountService discountService;
    private final OrderOfTableService orderOfTableService;
    private final PersonService personService;
    private final WebSocketService webSocketService;
    private final MessageService messageService;
    @Value("${timezone.name}")
    private String timezone;

    @Autowired
    public OrderService(OrderRepository orderRepository, ItemInOrderService itemInOrderService, MemberService memberService, DiscountService discountService, OrderOfTableService orderOfTableService, PersonService personService, WebSocketService webSocketService, MessageService messageService) {
        this.orderRepository = orderRepository;
        this.itemInOrderService = itemInOrderService;
        this.memberService = memberService;
        this.discountService = discountService;
        this.orderOfTableService = orderOfTableService;
        this.personService = personService;
        this.webSocketService = webSocketService;
        this.messageService = messageService;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order initOrder(Order order) {
        order.setDate(LocalDate.now(ZoneId.of(timezone)));
        order.setHour(LocalTime.now(ZoneId.of(timezone)));
        order.setStatus(OrderStatus.ACCEPTED);
        order.setAlreadyPaid((float) 0);
        order.setOriginalTotalPrice((float) 0);
        order.setTotalPriceToPay((float) 0.0);
        return order;
    }

    public Order addOrder(Order order) {
        Order o = initOrder(order);
        Order orderInDB = orderRepository.save(o);
        orderInDB.getItems().forEach(i -> {
            i.setOrder(orderInDB);
            itemInOrderService.addItemToOrder(i);
        });
        calculateTotalPrices(orderInDB);
        return orderRepository.save(orderInDB);
    }

    public Order addItemToOrder(Long orderId, ItemInOrder item) {
        Order order = getOrder(orderId);
        item.setOrder(order);
        ItemInOrder itemInOrder = itemInOrderService.addItemToOrder(item);
        order.getItems().add(itemInOrder);
        calculateTotalPrices(order);
        order = orderRepository.save(order);
        webSocketService.notifyExternalOrders(order);
        return order;
    }

    public Order deleteItemsListFromOrder(List<Long> itemsInOrderId) {
        if (itemsInOrderId == null || itemsInOrderId.isEmpty())
            throw new BadRequestException( "List of items is missing");
        Order order = itemInOrderService.getItemInOrderById(itemsInOrderId.get(0)).getOrder();
        itemInOrderService.deleteItemsListFromOrder(itemsInOrderId);
        calculateTotalPrices(order);
        order = orderRepository.save(order);
        webSocketService.notifyExternalOrders(order);
        return order;
    }

    public Order addItemsListToOrder(Long orderId, List<ItemInOrder> items) {
        Order order = getOrder(orderId);
        List<ItemInOrder> itemsInOrder = itemInOrderService.addListOfItemsToOrder(items, order);
        order.getItems().addAll(itemsInOrder);
        //  order.setOriginalTotalPrice(calculateTotalPrice(order));
        calculateTotalPrices(order);
        order = orderRepository.save(order);
        webSocketService.notifyExternalOrders(order);
        return order;
    }

    public void calculateTotalPrices(Order order) {
        // Check for discounts
        initializeItemsPrice(order);
        if (order.getPerson() != null && memberService.isMember(order.getPerson().getPhoneNumber()))
            membersCheckIfEntitledForDiscount(order);
        else
            checkIfEntitledForDiscount(order);

        // Calculate prices
        Float priceToPay = order.getItems()
                .stream()
                .map(ItemInOrder::getPrice)
                .reduce((float) 0, Float::sum);
        Float originalTotalPrice =
                order.getItems()
                        .stream()
                        .map(i -> i.getItem().getPrice())
                        .reduce((float) 0, Float::sum);
        order.setTotalPriceToPay(priceToPay);
        order.setOriginalTotalPrice(originalTotalPrice);
    }

    public Order payment(Long orderId, Float amount) {
        if (amount <= 0)
            throw new BadRequestException( "Please provide valid payment amount.");
        Order order = getOrder(orderId);
        amount = normalizeAmount(amount, order);
        if (order.getTotalPriceToPay() < amount + order.getAlreadyPaid())
            throw new BadRequestException( "You can't pay more then the remaining amount.");
        order.setAlreadyPaid(order.getAlreadyPaid() + amount);
        if (order.getAlreadyPaid().equals(order.getTotalPriceToPay())) {
            orderOfTableService.closeIfOrderOfTable(order);
        }
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    private Float normalizeAmount(Float amountReceived, Order order) {
        Float remainingAmount = order.getTotalPriceToPay() - order.getAlreadyPaid();
        if (Math.abs(amountReceived - remainingAmount) < 0.1) {
            return remainingAmount;
        }
        return amountReceived;
    }

    public Order updateComment(Long orderId, String comment) {
        Order order = getOrder(orderId);
        order.setOrderComment(comment);
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public Order updateTotalPrice(Long orderId, Float amount) {
        Order order = getOrder(orderId);
        if (amount > order.getOriginalTotalPrice())
            throw new BadRequestException("The amount is bigger than the total price");
        order.setTotalPriceToPay(order.getOriginalTotalPrice() - amount);
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public Order applyMemberDiscount(Long orderId, String phoneNumber) {
        Order order = getOrder(orderId);
        Member member = memberService.getMemberByPhoneNumber(phoneNumber);
        order.setPerson(member);
        calculateTotalPrices(order);
        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(  "There is no order with the id: " + orderId));
    }

    public List<Order> getOrdersByDatesAndHours(String startDateStr, String endDateStr, String startTimeStr, String endTimeStr) {
        try {
            LocalDate startDate = Utils.parseToLocalDate(startDateStr);
            LocalDate endDate = Utils.parseToLocalDate(endDateStr);
            LocalTime startTime = Utils.parseToLocalTime(startTimeStr);
            LocalTime endTime = Utils.parseToLocalTime(endTimeStr);
            return orderRepository.findByDateIsBetweenAndHourIsBetween(startDate, endDate, startTime, endTime);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BadRequestException( "The request was in bad format");
        }
    }

    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = getOrder(orderId);
        if (status.equals(OrderStatus.CLOSED) && !order.getAlreadyPaid().equals(order.getTotalPriceToPay()))
            throw new ConflictException( "This order currently cannot be closed because it first need to pay the bill.");
        order.setStatus(status);
        order = orderRepository.save(order);
        webSocketService.notifyExternalOrders(order);
        webSocketService.notifyMemberOrder(order);
        messageService.sendMessages(order.getPerson(), "Your Order", "Your Order is now " + status + ". Thank you for choosing Smart Food !");
        return order;
    }

    public Order updatePerson(Long orderId, Person person) {
        Order order = getOrder(orderId);
        connectPersonToOrder(order, person);
        order = orderRepository.save(order);
        webSocketService.notifyExternalOrders(order);
        return order;
    }

    public Order connectPersonToOrder(Order order, Person personToSave) {
        if (personToSave.getId() == null) {
            Optional<Person> op = personService.getOptionalPersonByPhone(personToSave.getPhoneNumber());
            if (op.isPresent()) {
                Person p = op.get();
                //    var person = order.getPerson();
                personToSave.setId(p.getId());
                order.setPerson(personService.savePerson(personToSave));
            } else {
                Person p = personService.savePerson(personToSave);
                order.setPerson(p);
            }
        } else {
            Person p = personService.updatePerson(personToSave);
            order.setPerson(p);
        }
        return order;
    }

    public void deleteOrder(Long orderId) {
        Order order = getOrder(orderId);
        orderRepository.delete(order);
        webSocketService.notifyExternalOrders(order);
    }

    public List<Order> getOrdersByDates(String startDate, String endDate) {
        return getOrdersByDatesAndHours(startDate, endDate, "00:00", "23:59");
    }

    public Order updateItemInOrder(ItemInOrder item) {
        ItemInOrder i = itemInOrderService.updateItemInOrder(item);
        Order order = getOrder(i.getOrder().getId());
        calculateTotalPrices(order);
        webSocketService.notifyExternalOrders(order);
        return orderRepository.save(order);
    }

    public void deleteItemFromOrder(Long itemId) {
        ItemInOrder itemInOrder = itemInOrderService.getItemInOrderById(itemId);
        Order order = itemInOrder.getOrder();
        itemInOrderService.deleteItemFromOrder(itemId);
        calculateTotalPrices(order);
        webSocketService.notifyExternalOrders(order);
        orderRepository.save(order);
    }

    public void checkIfEntitledForDiscount(Order order) {
        log.debug("checkIfEntitledForDiscount");

        List<Discount> relevantDiscounts = discountService.getDateRelevantDiscountsForOrder(order);
        relevantDiscounts.forEach(discount -> discount.getCategories().forEach(category -> {
            List<ItemInOrder> itemsInCategory = itemsInOrderByCategory(order, category);
            int numberOfItemsForDiscount = itemsInCategory.size() / (discount.getIfYouOrder() + discount.getYouGetDiscountFor());
            for (int i = 0; i < numberOfItemsForDiscount; i++) {
                applyItemInOrderDiscount(itemsInCategory.get(i), discount.getPercent());
            }
        }));

        orderRepository.save(order);
    }

    public void membersCheckIfEntitledForDiscount(Order order) {
        if (!memberService.isMember(order.getPerson().getPhoneNumber()))
            return;
        log.debug("membersCheckIfEntitledForDiscount");
        List<Discount> relevantDiscounts = discountService.getAllDateRelevantDiscountsForOrder(order);
        relevantDiscounts.forEach(discount -> discount.getCategories().forEach(category -> {
            List<ItemInOrder> itemsInCategory = itemsInOrderByCategory(order, category);
            int numberOfItemsForDiscount = itemsInCategory.size() / (discount.getIfYouOrder() + discount.getYouGetDiscountFor());
            for (int i = 0; i < numberOfItemsForDiscount; i++) {
                if (!discount.getForMembersOnly())
                    applyItemInOrderDiscount(itemsInCategory.get(i), discount.getPercent());
                else
                    applyItemInOrderAdditionalDiscount(itemsInCategory.get(i), discount.getPercent());
            }
        }));

        orderRepository.save(order);
    }

    private void initializeItemsPrice(Order order) {
        order.getItems().forEach(itemInOrder -> {
            itemInOrder.setPrice(itemInOrder.getItem().getPrice());
            log.debug("initializeItemsPrice : " + itemInOrder.getId() + " " + itemInOrder.getItem().getName() + " : " + itemInOrder.getPrice());
        });

    }

    public List<ItemInOrder> itemsInOrderByCategory(Order order, ItemCategory category) {
        return order.getItems()
                .stream()
                .filter(itemInOrder -> itemInOrder.getItem().getCategory().equals(category) && itemInOrder.getPrice() > 0)
                .sorted(Comparator.comparing(itemInOrder -> itemInOrder.getItem().getPrice())) // maybe MenuItem getPrice
                .collect(Collectors.toList());
    }

    private void applyItemInOrderDiscount(ItemInOrder itemInOrder, int percent) {
        log.debug("applyItemInOrderDiscount: " + percent + " - " + itemInOrder.toString());
        itemInOrder.setPrice(itemInOrder.getItem().getPrice() * ((100 - percent) / (float) 100)); // maybe MenuItem getPrice
    }

    private void applyItemInOrderAdditionalDiscount(ItemInOrder itemInOrder, int percent) {
        log.debug("old price: " + itemInOrder.getPrice());
        itemInOrder.setPrice(itemInOrder.getPrice() * ((100 - percent) / (float) 100)); // maybe MenuItem getPrice
        log.debug("new price: " + itemInOrder.getPrice());
    }


}
