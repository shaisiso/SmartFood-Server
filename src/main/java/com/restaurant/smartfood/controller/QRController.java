package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.RestaurantTable;
import com.restaurant.smartfood.service.QRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/qr")
public class QRController {
    @Autowired
    private QRService qrService;

    @GetMapping
    public String generateTokenForQR(@RequestParam Integer tableId, @RequestParam Integer daysForExpiration){
        return qrService.generateTokenForQR(tableId,daysForExpiration);
    }
    @PostMapping
    public RestaurantTable verifyToken(@RequestBody String token){
       return qrService.verifyToken(token);
    }
}
