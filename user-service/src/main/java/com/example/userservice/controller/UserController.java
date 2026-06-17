package com.example.userservice.controller;

import com.example.userservice.model.UserDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAllUsers() {
        List<EntityModel<UserDto>> userEntityModels = userService
                .getAllUsers()
                .stream()
                .map(userDto -> EntityModel.of(userDto,
                        linkTo(UserController.class)
                        .slash(userDto.id())
                        .withSelfRel()))
                .toList();
        var collectionModel = CollectionModel.of(userEntityModels);
        collectionModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        return collectionModel;
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
