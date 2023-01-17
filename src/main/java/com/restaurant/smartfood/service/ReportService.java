package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.repostitory.CancelItemRequestRepository;
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
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@Slf4j
public class ReportService {
    private final OrderService orderService;
    private final CancelItemRequestRepository cancelRequestRepository;
    private final OrderOfTableService orderOfTableService;
    private final DeliveryService deliveryService;
    private final TakeAwayService takeAwayService;
    @Autowired
    public ReportService(OrderService orderService, CancelItemRequestRepository cancelRequestRepository, OrderOfTableService orderOfTableService, DeliveryService deliveryService, TakeAwayService takeAwayService) {
        this.orderService = orderService;
        this.cancelRequestRepository = cancelRequestRepository;
        this.orderOfTableService = orderOfTableService;
        this.deliveryService = deliveryService;
        this.takeAwayService = takeAwayService;
    }

    public List<DailyColumnReport<Double>> getIncomeDailyColumnReport(String startDateSt, String endDateSt) {
        LocalDate startDate = parseDate(startDateSt);
        LocalDate endDate = parseDate(endDateSt);
        List<DailyColumnReport<Double>> incomeList = new ArrayList<>();
        List<Order> orders = getSortedOrdersByDates(startDateSt, endDateSt);
        LocalDate checkDate = startDate;
        while (checkDate.compareTo(endDate) <= 0) {
            LocalDate finalCheckDate = checkDate;
            double sumOfDailyIncome = orders.stream()
                    .filter(o -> o.getDate().equals(finalCheckDate))
                    .mapToDouble(Order::getAlreadyPaid)
                    .sum();
            DailyColumnReport column = DailyColumnReport.<Double>builder()
                    .date(checkDate)
                    .dayOfWeek(checkDate.getDayOfWeek())
                    .value(sumOfDailyIncome)
                    .build();
            incomeList.add(column);
            checkDate = checkDate.plusDays(1);
        }
        return incomeList;
    }


    public List<MonthlyColumnReport<Double>> getIncomeMonthlyColumnReport(String startDateSt, String endDateSt) {
        LocalDate startDate = parseDate(startDateSt);
        LocalDate endDate = parseDate(endDateSt);
        List<MonthlyColumnReport<Double>> incomeList = new ArrayList<>();
        List<Order> orders = getSortedOrdersByDates(startDateSt, endDateSt);
        LocalDate checkDate = startDate;
        while (checkDate.compareTo(endDate) <= 0) {
            LocalDate finalCheckDate = checkDate;
            double sumOfMonthlyIncome = orders.stream()
                    .filter(o -> o.getDate().getMonth().equals(finalCheckDate.getMonth()) && o.getDate().getYear() == finalCheckDate.getYear())
                    .mapToDouble(Order::getAlreadyPaid)
                    .sum();
            MonthlyColumnReport column = MonthlyColumnReport.<Double>builder()
                    .month(checkDate.getMonth())
                    .value(sumOfMonthlyIncome)
                    .build();
            incomeList.add(column);
            checkDate = checkDate.plusMonths(1);
        }
        return incomeList;
    }

    private List<Order> getSortedOrdersByDates(String startDateSt, String endDateSt) {
        List<Order> orders = orderService.getOrdersByDates(startDateSt, endDateSt);
        orders.sort(Comparator.comparing(Order::getDate));
        return orders;
    }

    public Map<String, Integer> getCanceledItemsReport(String startDateSt, String endDateSt) {
        LocalDateTime startDateTime = parseDate(startDateSt).atStartOfDay();
        LocalDateTime endDateTime = parseDate(endDateSt).atTime(23, 59);
        Map<String, Integer> itemsCanceledMap = new HashMap<>();
        List<CancelItemRequest> cancelItemRequests = cancelRequestRepository.findByDateIsBetween(startDateTime, endDateTime);
        cancelItemRequests.stream()
                .forEach(cr -> {
                    String menuItemName = cr.getMenuItem().getName();
                    if (itemsCanceledMap.containsKey(menuItemName))
                        itemsCanceledMap.put(menuItemName, itemsCanceledMap.get(menuItemName) + 1);
                    else
                        itemsCanceledMap.put(menuItemName, 1);
                });
        return itemsCanceledMap;
    }

    public Map<String, Integer> getOrderedItemsReport(String startDate, String endDate) {
        Map<String, Integer> orderedItemsMap = new HashMap<>();
        List<Order> orders = orderService.getOrdersByDates(startDate, endDate);
        orders.stream()
                .flatMap(order -> order.getItems().stream().map(itemInOrder -> itemInOrder.getItem().getName()))
                .forEach(menuItemName -> {
                    if (orderedItemsMap.containsKey(menuItemName))
                        orderedItemsMap.put(menuItemName, orderedItemsMap.get(menuItemName) + 1);
                    else
                        orderedItemsMap.put(menuItemName, 1);
                });
        return orderedItemsMap;
    }

    private LocalDate parseDate(String date) {
        try {
            return Utils.parseToLocalDate(date);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }


    public Map<String,List<DailyColumnReport<Long>>> getOrdersDailyReport(String startDateSt, String endDateSt) {
        LocalDate startDate = parseDate(startDateSt);
        LocalDate endDate = parseDate(endDateSt);
        Map<String,List<DailyColumnReport<Long>>> ordersMap = new HashMap<>();
        ArrayList<DailyColumnReport<Long>> deliveriesColumn = new ArrayList<>();
        ArrayList<DailyColumnReport<Long>>  taColumn = new ArrayList<>();
        ArrayList<DailyColumnReport<Long>>  tablesColumn = new ArrayList<>();
        List<Delivery> deliveryList = getSortedDeliveriesByDates(startDateSt, endDateSt);
        List<TakeAway> takeAwayList = getSortedTAByDates(startDateSt, endDateSt);
        List<OrderOfTable> orderOfTableList = getSortedOrdersOfTableByDates(startDateSt, endDateSt);
        LocalDate checkDate = startDate;
        while (checkDate.compareTo(endDate) <= 0) {
            LocalDate finalCheckDate = checkDate;
            long totalDeliveries = deliveryList.stream()
                    .filter(o -> o.getDate().equals(finalCheckDate))
                    .count();
            long totalTakeAway = takeAwayList.stream()
                    .filter(o -> o.getDate().equals(finalCheckDate))
                    .count();
            long totalOrdersOfTable = orderOfTableList.stream()
                    .filter(o -> o.getDate().equals(finalCheckDate))
                    .count();
            DailyColumnReport columnDelivery = DailyColumnReport.<Long>builder()
                    .date(checkDate)
                    .dayOfWeek(checkDate.getDayOfWeek())
                    .value(totalDeliveries)
                    .build();
            DailyColumnReport columnTA = DailyColumnReport.<Long>builder()
                    .date(checkDate)
                    .dayOfWeek(checkDate.getDayOfWeek())
                    .value(totalTakeAway)
                    .build();
            DailyColumnReport columnTables = DailyColumnReport.<Long>builder()
                    .date(checkDate)
                    .dayOfWeek(checkDate.getDayOfWeek())
                    .value(totalOrdersOfTable)
                    .build();

            deliveriesColumn.add(columnDelivery);
            taColumn.add(columnTA);
            tablesColumn.add(columnTables);

            checkDate = checkDate.plusDays(1);
        }
        ordersMap.put("Tables",tablesColumn);
        ordersMap.put("Deliveries",deliveriesColumn);
        ordersMap.put("TA",taColumn);
        return ordersMap;
    }

    public Map<String, List<MonthlyColumnReport<Long>>> getOrdersMonthlyReport(String startDateSt, String endDateSt) {
        LocalDate startDate = parseDate(startDateSt);
        LocalDate endDate = parseDate(endDateSt);
        Map<String,List<MonthlyColumnReport<Long>>> ordersMap = new HashMap<>();
        ArrayList<MonthlyColumnReport<Long>> deliveriesColumn = new ArrayList<>();
        ArrayList<MonthlyColumnReport<Long>>  taColumn = new ArrayList<>();
        ArrayList<MonthlyColumnReport<Long>>  tablesColumn = new ArrayList<>();
        List<Delivery> deliveryList = getSortedDeliveriesByDates(startDateSt, endDateSt);
        List<TakeAway> takeAwayList = getSortedTAByDates(startDateSt, endDateSt);
        List<OrderOfTable> orderOfTableList = getSortedOrdersOfTableByDates(startDateSt, endDateSt);
        LocalDate checkDate = startDate;
        while (checkDate.compareTo(endDate) <= 0) {
            LocalDate finalCheckDate = checkDate;
            Long totalDeliveries = deliveryList.stream()
                    .filter(o -> o.getDate().getMonth().equals(finalCheckDate.getMonth()) && o.getDate().getYear() == finalCheckDate.getYear())
                    .count();
            Long totalTakeAway = takeAwayList.stream()
                    .filter(o -> o.getDate().getMonth().equals(finalCheckDate.getMonth()) && o.getDate().getYear() == finalCheckDate.getYear())
                    .count();
            Long totalOrdersOfTable = orderOfTableList.stream()
                    .filter(o -> o.getDate().getMonth().equals(finalCheckDate.getMonth()) && o.getDate().getYear() == finalCheckDate.getYear())
                    .count();
            MonthlyColumnReport columnDelivery = MonthlyColumnReport.<Long>builder()
                    .month(checkDate.getMonth())
                    .value(totalDeliveries)
                    .build();
            MonthlyColumnReport columnTA = MonthlyColumnReport.<Long>builder()
                    .month(checkDate.getMonth())
                    .value(totalTakeAway)
                    .build();
            MonthlyColumnReport columnTables = MonthlyColumnReport.<Long>builder()
                    .month(checkDate.getMonth())
                    .value(totalOrdersOfTable)
                    .build();

            deliveriesColumn.add(columnDelivery);
            taColumn.add(columnTA);
            tablesColumn.add(columnTables);

            checkDate = checkDate.plusMonths(1);
        }
        ordersMap.put("Tables",tablesColumn);
        ordersMap.put("Deliveries",deliveriesColumn);
        ordersMap.put("TA",taColumn);
        return ordersMap;
    }
    private List<Delivery> getSortedDeliveriesByDates(String startDateSt, String endDateSt) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByDates(startDateSt, endDateSt);
        deliveries.sort(Comparator.comparing(Order::getDate));
        return deliveries;
    }
    private List<TakeAway> getSortedTAByDates(String startDateSt, String endDateSt) {
        List<TakeAway> ta = takeAwayService.getTakeAwayListByDates(startDateSt, endDateSt);
        ta.sort(Comparator.comparing(Order::getDate));
        return ta;
    }
    private List<OrderOfTable> getSortedOrdersOfTableByDates(String startDateSt, String endDateSt) {
        List<OrderOfTable> ordersOfTable = orderOfTableService.getOrdersOfTablesByDates(startDateSt, endDateSt);
        ordersOfTable.sort(Comparator.comparing(Order::getDate));
        return ordersOfTable;
    }

}
