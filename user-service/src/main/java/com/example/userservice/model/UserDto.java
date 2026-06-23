package com.example.userservice.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
        Long id,
        @Size(min = 4, max = 20, message = "Name shouldn't be shorter than 4 symbols " +
                "or longer than 20 symbols")
        @NotBlank(message = "Name can't be empty")
        String name,
        @Size(min = 4, max = 100, message = "Email shouldn't be shorter than 4 symbols " +
                "or longer than 100 symbols")
        @NotBlank(message = "Email can't be empty")
        @Email(message = "Incorrect email format")
        String email,
        @Min(value = 18, message = "User should be at least 18 years old")
        int age) {

        public UserDto(String name, String email, int age) {
                this(null, name, email, age);
        }
}