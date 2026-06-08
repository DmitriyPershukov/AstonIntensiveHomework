package com.example.user_service.controller;

import com.example.user_service.model.UserDto;
import com.example.user_service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(
        @PathVariable Long userId
    ){
        return userService.getUserById(userId);
    }

    @PostMapping
    public void createUser(
        @RequestBody @Valid UserDto user
    ){
        userService.createUser(user);
    }

    @PutMapping("/{userId}")
    public void updateUser(
        @PathVariable Long userId,
        @RequestBody @Valid UserDto user
    ){
        userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(
        @PathVariable Long userId
    ){
        userService.deleteUser(userId);
    }
}
