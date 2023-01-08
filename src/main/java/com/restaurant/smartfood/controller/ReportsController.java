package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.MenuItem;
import com.restaurant.smartfood.service.ReportService;
import com.restaurant.smartfood.utility.DailyColumnReport;
import com.restaurant.smartfood.utility.MonthlyColumnReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/reports")
public class ReportsController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/income/daily/{startDate}/{endDate}")
    public List<DailyColumnReport<Double>> getIncomeDailyColumnReport(@PathVariable("startDate") String startDate,
                                                              @PathVariable("endDate")String endDate){
        return reportService.getIncomeDailyColumnReport(startDate,endDate);
    }
    @GetMapping("/income/monthly/{startDate}/{endDate}")
    public List<MonthlyColumnReport<Double>> getIncomeMonthlyColumnReport(@PathVariable("startDate") String startDate,
                                                                  @PathVariable("endDate")String endDate){
        return reportService.getIncomeMonthlyColumnReport(startDate,endDate);
    }
    @GetMapping("/menu/canceled/{startDate}/{endDate}")
    @ResponseBody
    public Map<String, Integer> getCanceledItemsReport(@PathVariable("startDate") String startDate,
                                                         @PathVariable("endDate")String endDate){
        return reportService.getCanceledItemsReport(startDate,endDate);
    }
    @GetMapping("/menu/ordered/{startDate}/{endDate}")
    @ResponseBody
    public Map<String, Integer> getOrderedItemsReport(@PathVariable("startDate") String startDate,
                                                       @PathVariable("endDate")String endDate){
        return reportService.getOrderedItemsReport(startDate,endDate);
    }
    @GetMapping("/orders/daily/{startDate}/{endDate}")
    @ResponseBody
    public Map<String,List<DailyColumnReport<Long>>> getOrdersDailyReport(@PathVariable("startDate") String startDate,
                                                      @PathVariable("endDate")String endDate){
        return reportService.getOrdersDailyReport(startDate,endDate);
    }
    @GetMapping("/orders/monthly/{startDate}/{endDate}")
    @ResponseBody
    public Map<String,List<MonthlyColumnReport<Long>>> getOrdersMonthlyReport(@PathVariable("startDate") String startDate,
                                                                          @PathVariable("endDate")String endDate){
        return reportService.getOrdersMonthlyReport(startDate,endDate);
    }
}
