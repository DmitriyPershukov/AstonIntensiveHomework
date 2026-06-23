package com.example.userservice.controller;

import com.example.userservice.model.UserDto;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

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

    @Operation(summary = "Get all users")
    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAllUsers() {
        return representationModelAssembler.toCollectionModel(userService.getAllUsers());
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{userId}")
    public EntityModel<UserDto> getUser(
        @Parameter(description = "Id of the user to be retrieved", required = true)
        @PathVariable Long userId
    ){
        return representationModelAssembler.toModel(userService.getUserById(userId));
    }

    @Operation(summary = "Create new user")
    @PostMapping
    public void createUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "User to create", required = true,
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = UserDto.class),
        examples = @ExampleObject(value =
                "{\"name\": \"John\", " +
                "\"email\": \"john@email.com\", " +
                "\"age\": \"18\"}")))
        @RequestBody @Valid UserDto user
    ){
        userService.createUser(user);
    }


    @Operation(summary = "Replace user with certain id with provided user")
    @PutMapping("/{userId}")
    public void updateUser(
        @Parameter(description = "Id of the user to be replaced", required = true)
        @PathVariable Long userId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "User to replace old user", required = true,
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = UserDto.class),
        examples = @ExampleObject(value =
                "{\"name\": \"John\", " +
                "\"email\": \"john@email.com\", " +
                "\"age\": \"18\"}")))
        @RequestBody @Valid UserDto user
    ){
        userService.updateUser(userId, user);
    }

    @Operation(summary = "Delete user by id")
    @DeleteMapping("/{userId}")
    public void deleteUser(
        @Parameter(description = "Id of the user to be deleted", required = true)
        @PathVariable Long userId
    ){
        userService.deleteUser(userId);
    }
}
