package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Discount;
import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiscountServiceTest {

    @MockBean
    private DiscountService discountService;

    @Value("${timezone.name}")
    private String timezone;
    Discount discount;

    @BeforeEach
    void setUp() {
        DayOfWeek dayOfWeek = LocalDate.now(ZoneId.of(timezone)).getDayOfWeek();
        discount = Discount.builder()
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

        Mockito.doCallRealMethod().when(discountService).addDiscount(discount);
        Mockito.when(discountService.isDiscountOverLap(discount)).thenReturn(true);

    }

    @Test
    @DisplayName("add discount that overlaps")
    void addDiscount() {
        assertThrows(ConflictException.class,() -> {
            discountService.addDiscount(discount);
        });
    }
}