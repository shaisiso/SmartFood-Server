package com.restaurant.smartfood.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.smartfood.entities.Employee;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegisteredUserService {
    private final EmployeeRepository employeeRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthentication jwtAuthentication;
    private final JwtAuthorization jwtAuthorization;

    public ResponseEntity<AuthorizationTokens> login(LoginAuthenticationRequest credentials) {
        var user = loadUser(credentials.getPhoneNumber());
        validatePassword(user, credentials);
        var tokens = jwtAuthentication.createTokens(user);
        return tokens;
    }

    private RegisteredUserPrincipal loadUser(String phoneNumber) {
        var employeeOp = employeeRepository.findByPhoneNumber(phoneNumber);
        if (employeeOp.isPresent()) {
            return new RegisteredUserPrincipal(employeeOp.get());
        } else {
            var member = memberRepository.findByPhoneNumber(phoneNumber)
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

    public ResponseEntity<AuthorizationTokens>  refreshToken(HttpServletRequest request) throws IOException {
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is missing");
        }
        try {
            // verify token
            var decodedJWT = jwtAuthorization.verifyToken(request);
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
