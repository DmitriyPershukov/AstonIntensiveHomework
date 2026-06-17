package com.example.notificationservice.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleException(ConstraintViolationException ex) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Following parameter validation errors occurred:\n");
        ex.getConstraintViolations()
                .forEach(violation -> messageBuilder.append(violation.getMessage() + "\n"));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageBuilder);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity handleException(IOException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Service failed to send notification.");
    }
}
