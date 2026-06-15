package com.example.userservice.service;

import com.example.userservice.message.MessageProducer;
import com.example.userservice.model.UserDto;
import com.example.userservice.model.UserMappingUtils;
import com.example.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import com.example.userservice.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.example.userservice.model.UserMappingUtils.mapToUserDto;
import static com.example.userservice.model.UserMappingUtils.mapToUserEntity;

@Service
public class UserService {
    private static final String ENTITY_NOT_FOUND_MESSAGE_TEMPLATE = "User with id=%s doesn't exist.";
    private UserRepository userRepository;
    private MessageProducer messageProducer;

    public UserService(UserRepository userRepository, MessageProducer messageProducer){
        this.userRepository = userRepository;
        this.messageProducer = messageProducer;
    }

    public List<UserDto> getAllUsers(){
        List<User> users = StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .toList();
        return UserMappingUtils.mapToListUserDto(new ArrayList<>(users));
    }

    public UserDto getUserById(Long id){
        return mapToUserDto(
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        String.format(ENTITY_NOT_FOUND_MESSAGE_TEMPLATE, id))));
    }

    public void createUser(UserDto userDto){
        userRepository.save(mapToUserEntity(userDto));
        messageProducer.send(String.format("created %s", userDto.email()));
    }

    public void updateUser(Long id, UserDto userDto){
        User updatedUser = mapToUserEntity(userDto);
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format(ENTITY_NOT_FOUND_MESSAGE_TEMPLATE, id)));
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setAge(updatedUser.getAge());
        userRepository.save(user);
    }

    public void deleteUser(Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new EntityNotFoundException(String.format(ENTITY_NOT_FOUND_MESSAGE_TEMPLATE, id));
        }
        userRepository.deleteById(id);
        messageProducer.send(String.format("deleted %s", user.get().getEmail()));
    }

    public boolean exists(Long id){
        return userRepository.existsById(id);
    }
}
