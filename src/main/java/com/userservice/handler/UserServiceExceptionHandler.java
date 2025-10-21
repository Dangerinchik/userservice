package com.userservice.handler;

import com.userservice.dto.UserServiceErrorResponse;
import com.userservice.exception.CardInfoAlreadyExistsException;
import com.userservice.exception.CardInfoNotFoundException;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class UserServiceExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<UserServiceErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        String message = String.format("User not found: %s %s", LocalDateTime.now(), ex.getMessage());
        UserServiceErrorResponse response = new UserServiceErrorResponse(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({CardInfoNotFoundException.class})
    public ResponseEntity<UserServiceErrorResponse> handleCardInfoNotFound(CardInfoNotFoundException ex) {
        String message = String.format("Card Info not found: %s %s", LocalDateTime.now(), ex.getMessage());
        UserServiceErrorResponse response = new UserServiceErrorResponse(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<UserServiceErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        String message = String.format("User already exists: %s %s", LocalDateTime.now(), ex.getMessage());
        UserServiceErrorResponse response = new UserServiceErrorResponse(message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({CardInfoAlreadyExistsException.class})
    public ResponseEntity<UserServiceErrorResponse> handleCardInfoAlreadyExists(CardInfoAlreadyExistsException ex) {
        String message = String.format("Card Info already exists: %s %s", LocalDateTime.now(), ex.getMessage());
        UserServiceErrorResponse response = new UserServiceErrorResponse(message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<UserServiceErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = String.format("Argument not valid: %s %s", LocalDateTime.now(), ex.getMessage());
        UserServiceErrorResponse response = new UserServiceErrorResponse(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
