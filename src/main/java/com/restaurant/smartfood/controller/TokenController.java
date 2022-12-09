//package com.restaurant.smartfood.controller;
//
//import com.restaurant.smartfood.security.AuthorizationTokens;
//import com.restaurant.smartfood.security.RegisteredUserService;
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
//    private RegisteredUserService registeredUserService;
//
//
//    @PostMapping("/refresh")
//    public AuthorizationTokens refreshToken(HttpServletRequest request) throws IOException {
//       return registeredUserService.refreshToken(request);
//    }
//}
