package com.restaurant.smartfood.security;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Slf4j
@Component
public class AuthorizeAspect {
    private final  HttpServletRequest request; //Inject request to have header access
    private final JwtAuthorization jwtAuthorization;

    @Autowired
    public AuthorizeAspect(HttpServletRequest request, JwtAuthorization jwtAuthorization) {
        this.request = request;
        this.jwtAuthorization = jwtAuthorization;
    }

    @Before("@annotation(authorize)")
    public void authorizeAspect(Authorize authorize) {
        String[] roles = authorize.roles(); //Roles listed in annotation
        jwtAuthorization.authorizeRequest(request,roles);
    }
    @Before("@annotation(AuthorizeManagers)")
    public void authorizeManagersAspect() {
        String[] roles = {"ROLE_MANAGER","ROLE_SHIFT_MANAGER","ROLE_DELIVERY_MANAGER","ROLE_KITCHEN_MANAGER","ROLE_BAR_MANAGER"};
        jwtAuthorization.authorizeRequest(request,roles);
    }
    @Before("@annotation(AuthorizeEmployee)")
    public void authorizeEmployeeAspect() {
        String[] roles = {"ROLE_HOSTESS","ROLE_WAITER","ROLE_KITCHEN","ROLE_BAR","ROLE_DELIVERY_GUY",
                "ROLE_MANAGER","ROLE_SHIFT_MANAGER","ROLE_DELIVERY_MANAGER","KITCHEN_MANAGER","BAR_MANAGER"};
        jwtAuthorization.authorizeRequest(request,roles);
    }
    @Before("@annotation(AuthorizeGeneralManager)")
    public void authorizeGeneralManagerAspect() {
        String[] roles = {"ROLE_MANAGER"};
        jwtAuthorization.authorizeRequest(request,roles);
    }
    @Before("@annotation(AuthorizeMember)")
    public void authorizeMemberAspect() {
        String[] roles = {"ROLE_MEMBER","ROLE_MANAGER","ROLE_SHIFT_MANAGER","ROLE_DELIVERY_MANAGER","ROLE_KITCHEN_MANAGER","ROLE_BAR_MANAGER"};
        jwtAuthorization.authorizeRequest(request,roles);
    }
    @Before("@annotation(AuthorizeRegisteredUser)")
    public void authorizeRegisteredUserAspect() {
        String[] roles = {"ROLE_MEMBER","ROLE_HOSTESS","ROLE_WAITER","ROLE_KITCHEN","ROLE_BAR","ROLE_DELIVERY_GUY",
                "ROLE_MANAGER","ROLE_SHIFT_MANAGER","ROLE_DELIVERY_MANAGER","KITCHEN_MANAGER","BAR_MANAGER"};
        jwtAuthorization.authorizeRequest(request,roles);
    }

}
