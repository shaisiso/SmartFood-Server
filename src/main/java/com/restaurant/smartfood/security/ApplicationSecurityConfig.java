//package com.restaurant.smartfood.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private EmployeePrincipalDetailsService employeeDetailsService;
//
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authenticationProvider());
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
//        jwtAuthenticationFilter.setFilterProcessesUrl("/api/login");
//
//        http
//                // remove csrf and state in session because in jwt we do not need them
//                .csrf().disable()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                // add JWT filters (1. authentication, 2. authorization)
//                .authorizeRequests()
//                // configure access rules
//                .antMatchers(HttpMethod.POST, "/api/login/**", "/api/token/refresh","api/sms").permitAll()
//                .and()
//                .addFilter(jwtAuthenticationFilter)
//                .addFilterBefore(new JwtAuthorizationFilter(),JwtAuthenticationFilter.class);
//        // .addFilter(new JwtAuthorizationFilter(authenticationManager(),this.employeeRepository));
//        //  .anyRequest().authenticated();
//
//    }
//
//
//
//    @Bean
//    DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        daoAuthenticationProvider.setUserDetailsService(employeeDetailsService);
//        return daoAuthenticationProvider;
//    }
//
//
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        //return new Argon2PasswordEncoder()
//        return new BCryptPasswordEncoder();
//    }
//
//}
