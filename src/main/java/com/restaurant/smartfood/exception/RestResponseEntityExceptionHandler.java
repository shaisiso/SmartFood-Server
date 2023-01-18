package com.restaurant.smartfood.exception;


import com.restaurant.smartfood.security.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
@ResponseStatus
@Slf4j
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    /*
     * method for handle Object validation errors
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(ResourceNotFoundException exception,
                                                                  WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND,
                exception.getMessage(), request.getDescription(false));

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(message);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorMessage> unprocessableEntityException
            (UnprocessableEntityException exception, WebRequest request) {

        ErrorMessage message = new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage(), request.getDescription(false));

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(message);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorMessage> conflictException(ConflictException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT,
                exception.getMessage(), request.getDescription(false));

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(message);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> badRequestException(BadRequestException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
                exception.getMessage(), request.getDescription(false));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(message);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> accessDeniedException
    		(AccessDeniedException exception, WebRequest request){
        log.warn("AccessDeniedException");
        ErrorMessage message;
    	String header = request.getHeader(JwtProperties.HEADER_STRING);
    	if (header !=null && header.startsWith(JwtProperties.TOKEN_PREFIX))
    		message = new ErrorMessage(HttpStatus.FORBIDDEN.value(),HttpStatus.FORBIDDEN,
	    			exception.getMessage(),request.getDescription(false));
    	else
	    	message = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(),HttpStatus.UNAUTHORIZED,
	    			exception.getMessage(),request.getDescription(false));

    	return ResponseEntity.status(message.getStatus()).body(message);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> badCredentialsException(BadCredentialsException exception, WebRequest request){
        log.warn("BadCredentials");
        ErrorMessage message = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED,
                exception.getMessage(), request.getDescription(false));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(message);
    }

}
