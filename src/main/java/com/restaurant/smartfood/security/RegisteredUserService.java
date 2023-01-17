package com.restaurant.smartfood.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.RegisteredUser;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class RegisteredUserService {
    private final EmployeeRepository employeeRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthentication jwtAuthentication;
    private final JwtAuthorization jwtAuthorization;
    @Autowired
    public RegisteredUserService(EmployeeRepository employeeRepository, MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtAuthentication jwtAuthentication, JwtAuthorization jwtAuthorization) {
        this.employeeRepository = employeeRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthentication = jwtAuthentication;
        this.jwtAuthorization = jwtAuthorization;
    }

    public ResponseEntity<AuthorizationTokens> memberLogin(LoginAuthenticationRequest credentials) {
        RegisteredUserPrincipal memberUser = new RegisteredUserPrincipal(memberRepository.findByPhoneNumber(credentials.getPhoneNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials")));
        validatePassword(memberUser, credentials);
        ResponseEntity<AuthorizationTokens> tokens = jwtAuthentication.createTokens(memberUser);
        return tokens;
    }

    public ResponseEntity<AuthorizationTokens> employeeLogin(LoginAuthenticationRequest credentials) {
        RegisteredUserPrincipal employeeUser = new RegisteredUserPrincipal(employeeRepository.findByPhoneNumber(credentials.getPhoneNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials")));
        validatePassword(employeeUser, credentials);
        ResponseEntity<AuthorizationTokens> tokens = jwtAuthentication.createTokens(employeeUser);
        return tokens;
    }

    private RegisteredUserPrincipal loadUser(String phoneNumber) {
        Optional<Employee> employeeOp = employeeRepository.findByPhoneNumber(phoneNumber);
        if (employeeOp.isPresent()) {
            return new RegisteredUserPrincipal(employeeOp.get());
        } else {
            Member member = memberRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> {
                        log.error("User not found in the database");
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
                    });
            return new RegisteredUserPrincipal(member);
        }
    }

    private void validatePassword(RegisteredUserPrincipal registeredUser, LoginAuthenticationRequest credentials) {
        if (!passwordEncoder.matches(credentials.getPassword(), registeredUser.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
    }

    public ResponseEntity<AuthorizationTokens> refreshToken(AuthorizationTokens tokens) {
        String refreshToken = tokens.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is missing");
        }
        try {
            // verify token
            DecodedJWT decodedJWT = jwtAuthorization.verifyToken(refreshToken);
            String userPhone = decodedJWT.getSubject();
            // generate new access and refresh tokens
            UserDetails principal = loadUser(userPhone);
            // Create JWT Token
            return jwtAuthentication.createTokens(principal);
        } catch (SignatureVerificationException e) { // token was changed
            log.error("Authorization was failed. " + e.getMessage());
            log.error("Token was changed and cannot be trusted");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization was failed.");
        } catch (TokenExpiredException e) { // token expired
            log.error(e.toString());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired, need to re-login");
        } catch (Exception e) {
            log.error("The authorization failed for a certain reason");
            log.error(e.toString());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization was failed");
        }
    }

}
