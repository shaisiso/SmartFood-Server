package com.restaurant.smartfood.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class BaseController {

    @GetMapping("/")
    public ResponseEntity<?> getServiceName(){
        ResponseEntity<?> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        return responseEntity;
    }
}
