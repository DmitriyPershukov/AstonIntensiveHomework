package com.example.userservice.controller;

import com.example.userservice.model.UserDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private final SimpleRepresentationModelAssembler<UserDto> representationModelAssembler;

    public UserController(UserService userService,
                          SimpleRepresentationModelAssembler<UserDto> representationModelAssembler){
        this.userService = userService;
        this.representationModelAssembler = representationModelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAllUsers() {
        return representationModelAssembler.toCollectionModel(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public EntityModel<UserDto> getUser(
        @PathVariable Long userId
    ){
        return representationModelAssembler.toModel(userService.getUserById(userId));
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
