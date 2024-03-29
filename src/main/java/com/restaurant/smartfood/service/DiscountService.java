package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Discount;
import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.DiscountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final OrderService orderService;

    @Value("${timezone.name}")
    private String timezone;

    @Autowired
    public DiscountService(DiscountRepository discountRepository, @Lazy OrderService orderService) {
        this.discountRepository = discountRepository;
        this.orderService = orderService;
    }

    public Discount addDiscount(Discount discount) {
        if (isDiscountOverLap(discount))
            throw new ConflictException("There is an overlap with a discount");
        return discountRepository.save(discount);
    }

    public Discount updateDiscount(Discount discount) {
        if (discount.getDiscountId() == null)
            throw new BadRequestException("You have to have an id for the discount");
        discountRepository.findById(discount.getDiscountId()).orElseThrow(() ->
                new ResourceNotFoundException("There is no discount with the id " + discount.getDiscountId()));
        if (isDiscountOverLap(discount))
            throw new ConflictException("There is an overlap with a discount");
        return discountRepository.save(discount);
    }

    public void deleteDiscount(Long id) {
        Discount d = discountRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("There is no discount with the id " + id));
        discountRepository.delete(d);
    }

    public List<Discount> getDiscountsByDates(String startDate, String endDate) {
        try {
            LocalDate localStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate localEndDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return discountRepository.findByStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual(localEndDate, localStartDate);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BadRequestException( "The request was in bad format");
        }
    }

    public List<Discount> getDiscountsByCategory(ItemCategory category) {
        return discountRepository.findByCategories(category);
    }

    public List<Discount> getTodayDiscounts() {
        LocalDate today = LocalDate.now(ZoneId.of(timezone));
        return discountRepository.findByStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual
                (today, today);
    }

    public List<Discount> getDiscountsByDatesAndHours(String startDate, String endDate, String startHour, String endHour) {
        try {
            LocalDate localStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate localEndDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalTime localStartHour = LocalTime.parse(startHour, DateTimeFormatter.ofPattern("HH-mm"));
            LocalTime localEndHour = LocalTime.parse(endHour, DateTimeFormatter.ofPattern("HH-mm"));
            return getDiscountsByDatesAndHours(localStartDate, localEndDate, localStartHour, localEndHour);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BadRequestException( "The request was in bad format");
        }
    }

    public List<Discount> getDiscountsByDatesAndHours(LocalDate localStartDate, LocalDate localEndDate, LocalTime localStartHour, LocalTime localEndHour) {
        return discountRepository.findByDatesAndHours(localStartDate, localEndDate, localStartHour, localEndHour);
    }

    public List<Discount> getRelevantDiscountsForOrder(Long orderId, boolean isOnlyForMembers) {
        List<Discount> relevantDiscounts = new ArrayList<>();
        Order order = orderService.getOrder(orderId);
        getDateRelevantDiscountsForOrder(order, isOnlyForMembers)
                .forEach(discount -> discount.getCategories()
                        .stream()
                        .filter(category -> {
                            long relevantItemsNumber = order.getItems()
                                    .stream()
                                    .map(ItemInOrder::getItem)
                                    .filter(menuItem -> menuItem.getCategory().equals(category))
                                    .count();
                            return relevantItemsNumber >= discount.getIfYouOrder() + discount.getYouGetDiscountFor();
                        })
                        .findAny()
                        .ifPresent(c -> relevantDiscounts.add(discount)));
        return relevantDiscounts;

    }

    public List<Discount> getDateRelevantDiscountsForOrder(Order order) {
        return getDateRelevantDiscountsForOrder(order, false);
    }

    public List<Discount> getDateRelevantDiscountsForOrder(Order order, boolean isOnlyForMembers) {
        LocalDate dateNow = LocalDate.now(ZoneId.of(timezone));
        LocalTime timeNow = LocalTime.now(ZoneId.of(timezone));
        return discountRepository.findByDatesAndHours(order.getDate(), dateNow, order.getHour(), timeNow)
                .stream()
                .filter(d -> d.getDays().contains(dateNow.getDayOfWeek()) && d.getForMembersOnly() == isOnlyForMembers) // Members discount applied separately
                .collect(Collectors.toList());
    }

    public List<Discount> getAllDateRelevantDiscountsForOrder(Order order) {
        LocalDate dateNow = LocalDate.now(ZoneId.of(timezone));
        LocalTime timeNow = LocalTime.now(ZoneId.of(timezone));
        return discountRepository.findByDatesAndHours(order.getDate(), dateNow, order.getHour(), timeNow)
                .stream()
                .filter(d -> d.getDays().contains(dateNow.getDayOfWeek()))
                .sorted(Comparator.comparing(Discount::getForMembersOnly))
                .collect(Collectors.toList());

    }

    private boolean isDiscountOverLap(Discount discount) { // overlap is separate between members and rest
        List<Discount> overlappedDiscounts = getDiscountsByDatesAndHours(discount.getStartDate(), discount.getEndDate(),
                discount.getStartHour(), discount.getEndHour());
        for (Discount d : overlappedDiscounts) {
            boolean isOverlap = d.getDays()
                    .stream()
                    .anyMatch(day -> discount.getDays().contains(day))
                    && d.getCategories()
                    .stream()
                    .anyMatch(c -> discount.getCategories().contains(c))
                    && discount.getForMembersOnly().equals(d.getForMembersOnly());
            if (isOverlap)
                return true;
        }
        return false;
    }

}
