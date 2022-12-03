//package com.restaurant.smartfood.security;
//
//import com.restaurant.smartfood.entities.Employee;
//import com.restaurant.smartfood.entities.RegisteredUser;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//@Slf4j
//public class RegisteredUserPrincipal implements UserDetails {
//    /**
//     *
//     */
//    private static final long serialVersionUID = 1L;
//
//    private RegisteredUser registeredUser;
//
//    public RegisteredUserPrincipal(RegisteredUser registeredUser) {
//        this.registeredUser=registeredUser;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> authorities =new ArrayList<>();
//        if (registeredUser instanceof Employee){
//            var employee = (Employee)registeredUser;
//            authorities.add(new SimpleGrantedAuthority("ROLE_"+employee.getRole()));
//        }
//        else{
//            log.debug("Not employee");
//            authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
//        }
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return this.registeredUser.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return this.registeredUser.getPhoneNumber();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
