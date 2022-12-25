package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Discount;
import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.repostitory.DiscountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@Slf4j
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Value("${timezone.name}")
    private String timezone;

    public Discount addDiscount(Discount discount) {
        if (checkDiscountOverLap(discount))
            return discountRepository.save(discount);
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "There is a discount overlap");
    }

    public Discount updateDiscount(Discount discount) {
        if (discount.getDiscountId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You have to have an id for the discount");
        discountRepository.findById(discount.getDiscountId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no discount with the id " + discount.getDiscountId()));
        if (checkDiscountOverLap(discount))
            return discountRepository.save(discount);
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "There is a discount overlap");
    }

    public void deleteDiscount(Long id) {
        var d = discountRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no discount with the id " + id));
        discountRepository.delete(d);
    }

    public List<Discount> getDiscountsByDates(String startDate, String endDate) {
        try {
            LocalDate localStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate localEndDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return discountRepository.findByStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual(localEndDate, localStartDate);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public List<Discount> getDiscountsByCategory(ItemCategory category) {
        return discountRepository.findByCategories(category);
    }

    public List<Discount> getTodayDiscounts() {
        var today = LocalDate.now(ZoneId.of(timezone));
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public List<Discount> getDiscountsByDatesAndHours(LocalDate localStartDate, LocalDate localEndDate, LocalTime localStartHour, LocalTime localEndHour) {
        return discountRepository.findByDatesAndHours(localStartDate, localEndDate, localStartHour, localEndHour);
    }

    private boolean checkDiscountOverLap(Discount discount) {
        var overlappedDiscounts = getDiscountsByDatesAndHours(discount.getStartDate(), discount.getEndDate(),
                discount.getStartHour(), discount.getEndHour());

        for (var d : overlappedDiscounts) {
            var isOverlap = d.getDays()
                    .stream()
                    .anyMatch(day -> discount.getDays().contains(day)) &&
                    d.getCategories()
                            .stream()
                            .anyMatch(c -> discount.getCategories().contains(c));
            if (isOverlap)
                return false;
        }
        return true;
    }
}
