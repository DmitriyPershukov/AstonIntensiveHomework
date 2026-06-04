package com.example.user_service.model;

public class UserMappingUtils {
    public static UserDto mapToUserDto(User user){
        return new UserDto(user.getName(), user.getEmail(), user.getAge());
    }

    public static User mapToUserEntity(UserDto userDto){
        return new User(userDto.name(), userDto.email(), userDto.age());
    }
}
