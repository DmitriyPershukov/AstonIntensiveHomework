package com.example.user_service.service;

import com.example.user_service.model.UserDto;
import com.example.user_service.model.UserMappingUtils;
import com.example.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import com.example.user_service.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.example.user_service.model.UserMappingUtils.mapToUserDto;
import static com.example.user_service.model.UserMappingUtils.mapToUserEntity;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers(){
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(UserMappingUtils::mapToUserDto).toList();
    }

    public UserDto getUserById(Long id){
        return mapToUserDto(
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        String.format("User with id=%s doesn't exist.", id))));
    }

    public void createUser(UserDto userDto){
        userRepository.save(mapToUserEntity(userDto));
    }

    public void updateUser(Long id, UserDto userDto){
        User updatedUser = mapToUserEntity(userDto);
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("User with id=%s doesn't exist.", id)));
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setAge(updatedUser.getAge());
        userRepository.save(user);
    }

    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new EntityNotFoundException(String.format("User with id=%s doesn't exist.", id));
        }
        userRepository.deleteById(id);
    }

    public boolean exists(Long id){
        return userRepository.existsById(id);
    }
}
