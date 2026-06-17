package com.example.userservice.controller;

import com.example.userservice.model.UserDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

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
