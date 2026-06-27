package com.example.userservice.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLTransientConnectionException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handleException(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleException(DataIntegrityViolationException ex) {
        String exceptionMessage = ex.getMostSpecificCause().getMessage();
        String trimmedMessage = exceptionMessage.substring(exceptionMessage.indexOf("Detail")+8);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(trimmedMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleException(MethodArgumentNotValidException ex) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Following field validation errors occurred:\n");
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            messageBuilder.append(String.format("Error in field '%s': %s\n",
                    fieldError.getField(),
                    fieldError.getDefaultMessage()));
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageBuilder);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity handleException(CallNotPermittedException ex){
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service unavailable at the moment. Please try again later.");
    }

    @ExceptionHandler(SQLTransientConnectionException.class)
    public ResponseEntity handleException(SQLTransientConnectionException ex){
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Data storage is not accessible at the moment.");
    }
}
