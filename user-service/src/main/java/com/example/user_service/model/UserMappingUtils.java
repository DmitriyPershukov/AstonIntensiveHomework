package com.example.user_service.model;

import java.util.List;
import java.util.stream.StreamSupport;

public class UserMappingUtils {
    public static UserDto mapToUserDto(User user){
        return new UserDto(user.getName(), user.getEmail(), user.getAge());
    }

    public static User mapToUserEntity(UserDto userDto){
        return new User(userDto.name(), userDto.email(), userDto.age());
    }

    public static List<UserDto> mapToListUserDto(List<User> users){
        return users
                .stream()
                .map(UserMappingUtils::mapToUserDto)
                .toList();
    }
}
