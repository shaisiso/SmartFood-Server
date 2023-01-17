package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Discount;
import com.restaurant.smartfood.entities.ItemCategory;
import com.restaurant.smartfood.security.AuthorizeGeneralManager;
import com.restaurant.smartfood.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/discount")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @PostMapping
    @AuthorizeGeneralManager
    public Discount addDiscount(@RequestBody @Valid Discount discount) {
        return discountService.addDiscount(discount);
    }

    @PutMapping
    @AuthorizeGeneralManager
    public Discount updateDiscount(@RequestBody @Valid Discount discount) {
        return discountService.updateDiscount(discount);
    }

    @DeleteMapping("/{discountid}")
    @AuthorizeGeneralManager
    public void deleteDiscount(@PathVariable("discountid") Long id) {
        discountService.deleteDiscount(id);
    }

    @GetMapping("/dates/{startDate}/{endDate}")
    public List<Discount> getDiscountsByDates(@PathVariable("startDate") String startDate,
                                                      @PathVariable("endDate") String endDate) {
        return discountService.getDiscountsByDates(startDate, endDate);
    }

    @GetMapping("/dates/{startDate}/{endDate}/{startHour}/{endHour}")
    public List<Discount> getDiscountsByDatesAndHours(@PathVariable("startDate") String startDate,
                                                      @PathVariable("endDate") String endDate,
                                                      @PathVariable("startHour") String startHour,
                                                      @PathVariable("endHour") String endHour) {
        return discountService.getDiscountsByDatesAndHours(startDate, endDate, startHour, endHour);
    }

    @GetMapping("/category/{category}")
    public List<Discount> getDiscountsByCategory(@PathVariable("category") ItemCategory category) {
        return discountService.getDiscountsByCategory(category);
    }

    @GetMapping("/today")
    public List<Discount> getTodayDiscounts() {
        return discountService.getTodayDiscounts();
    }

    @GetMapping("/today/order/{orderId}")
    public List<Discount> getRelevantDiscountsForCurrentOrder(@PathVariable("orderId") Long orderId){
        return discountService.getRelevantDiscountsForOrder(orderId,false);
    }
    @GetMapping("/today/order/member/{orderId}")
    public List<Discount> getRelevantMemberDiscountsForCurrentOrder(@PathVariable("orderId") Long orderId){
        return discountService.getRelevantDiscountsForOrder(orderId,true);
    }
}
