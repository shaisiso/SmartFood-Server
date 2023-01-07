package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.service.ReportService;
import com.restaurant.smartfood.utility.DailyColumnReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/reports")
public class ReportsController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/daily/{startDate}/{endDate}")
    public List<DailyColumnReport> getIncomeDailyColumnReport(@PathVariable("startDate") String startDate,
                                                              @PathVariable("endDate")String endDate){
        return reportService.getIncomeDailyColumnReport(startDate,endDate);
    }
}
