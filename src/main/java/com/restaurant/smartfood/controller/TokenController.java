//package com.restaurant.smartfood.controller;
//
//import com.restaurant.smartfood.security.RegisteredUserPrincipalService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@CrossOrigin
//@RestController
//@RequestMapping("/api/token")
//public class TokenController {
//    @Autowired
//    private RegisteredUserPrincipalService registeredUserPrincipalService;
//
//
//    @PostMapping("/refresh")
//    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        registeredUserPrincipalService.refreshToken(request, response);
//    }
//}
