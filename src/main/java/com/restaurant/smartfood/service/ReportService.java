package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Order;
import com.restaurant.smartfood.utility.DailyColumnReport;
import com.restaurant.smartfood.utility.MonthlyColumnReport;
import com.restaurant.smartfood.utility.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReportService {
    private final OrderService orderService;

    public List<DailyColumnReport> getIncomeDailyColumnReport(String startDateSt, String endDateSt) {
        var startDate = parseDate(startDateSt);
        var endDate = parseDate(endDateSt);
        List<DailyColumnReport> incomeList = new ArrayList<>();
        List<Order> orders = getSortedOrdersByDates(startDateSt, endDateSt);
        var checkDate = startDate;
        while (checkDate.compareTo(endDate)<=0){
            LocalDate finalCheckDate = checkDate;
            var sumOfDailyIncome = orders.stream()
                    .filter(o->o.getDate().equals(finalCheckDate))
                    .mapToDouble(Order::getAlreadyPaid)
                    .sum();
            var column = DailyColumnReport.builder()
                    .date(checkDate)
                    .dayOfWeek(checkDate.getDayOfWeek())
                    .value(sumOfDailyIncome)
                    .build();
            incomeList.add(column);
            checkDate =checkDate.plusDays(1);
        }
        return incomeList;
    }


    public List<MonthlyColumnReport> getIncomeMonthlyColumnReport(String startDateSt, String endDateSt) {
        var startDate = parseDate(startDateSt);
        var endDate = parseDate(endDateSt);
        List<MonthlyColumnReport> incomeList = new ArrayList<>();
        List<Order> orders = getSortedOrdersByDates(startDateSt, endDateSt);
        var checkDate = startDate;
        while (checkDate.compareTo(endDate)<=0){
            LocalDate finalCheckDate = checkDate;
            var sumOfMonthlyIncome = orders.stream()
                    .filter(o->o.getDate().getMonth().equals(finalCheckDate.getMonth()) && o.getDate().getYear() == finalCheckDate.getYear())
                    .mapToDouble(Order::getAlreadyPaid)
                    .sum();
            var column = MonthlyColumnReport.builder()
                    .month(checkDate.getMonth())
                    .value(sumOfMonthlyIncome)
                    .build();
            incomeList.add(column);
            checkDate =checkDate.plusMonths(1);
        }
        return incomeList;
    }

    private List<Order> getSortedOrdersByDates(String startDateSt, String endDateSt) {
        var orders = orderService.getOrdersByDates(startDateSt, endDateSt);
        orders.sort(Comparator.comparing(Order::getDate));
        return orders;
    }
    private LocalDate parseDate(String date) {
        try {
            return Utils.parseToLocalDate(date);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

}
