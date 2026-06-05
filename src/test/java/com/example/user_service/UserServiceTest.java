package com.example.user_service;

import com.example.user_service.model.User;
import com.example.user_service.model.UserDto;
import com.example.user_service.model.UserMappingUtils;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import static com.example.user_service.model.UserMappingUtils.mapToUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private MockitoSession mockitoSession;

    @BeforeEach
    void setUp(){
        mockitoSession = Mockito.mockitoSession()
                        .initMocks(this)
                        .startMocking();
        userService = new UserService(userRepository);
    }

    @AfterEach
    void tearDown(){
        mockitoSession.finishMocking();
    }

    @Test
    void testDeleteUser(){
        Long id = 1L;
        when(userRepository.existsById(eq(1L))).thenReturn(true);
        userService.deleteUser(id);
        verify(userRepository).deleteById(eq(id));
    }

    @ParameterizedTest
    @MethodSource("supplyUsers")
    void testCreateUser(UserDto userDto){
        userService.createUser(userDto);
        verify(userRepository).save(eq(mapToUserEntity(userDto)));
    }

    static Stream<UserDto> supplyUsers(){
        return Stream.of(
            new UserDto("Martha", "martha@gmail.com", 35),
            new UserDto("John", "john@gmail.com", 44)
        );
    }

    @ParameterizedTest
    @MethodSource("supplyIdUsers")
    void testUpdateUser(Long id, UserDto userDto){
        when(userRepository.findById(eq(id))).thenReturn(Optional.ofNullable(mapToUserEntity(userDto)));
        userService.updateUser(id, userDto);
        verify(userRepository).save(eq(mapToUserEntity(userDto)));
    }

    @ParameterizedTest
    @MethodSource("supplyIdUsers")
    void testUpdateUserThrowsExceptionWhenOldUserNotFound(Long id, UserDto userDto){
        when(userRepository.findById(eq(id))).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.updateUser(id, userDto));
    }

    static Stream<Arguments> supplyIdUsers(){
        UserDto user1 = new UserDto("Martha", "martha@gmail.com", 35);
        UserDto user2 = new UserDto("John", "john@gmail.com", 44);
        return Stream.of(
                Arguments.of(1L, user1),
                Arguments.of(2L, user2)
        );
    }

    @ParameterizedTest
    @MethodSource("supplyIdUsers")
    void testGetUserById(Long id, UserDto userDto){
        when(userRepository.findById(eq(id))).thenReturn(Optional.ofNullable(mapToUserEntity(userDto)));
        Assertions.assertEquals(userDto, userService.getUserById(id));
    }

    @Test
    void testGetUserByIdThrowsExceptionWhenOldUserNotFound(){
        Long id = 1L;
        when(userRepository.findById(eq(id))).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void testGetAllUsers(){
        List<UserDto> userDtos = supplyUsers().toList();
        List<User> users = userDtos.stream().map(UserMappingUtils::mapToUserEntity).toList();
        when(userRepository.findAll()).thenReturn(users);
        Assertions.assertIterableEquals(userDtos, userService.getAllUsers());
    }

    @Test
    void testExistsReturnsTrueIfEntityFound(){
        when(userRepository.existsById(eq(1L))).thenReturn(true);
        Assertions.assertTrue(userService.exists(1L));
    }

    @Test
    void testExistsReturnsFalseIfEntityNotFound(){
        when(userRepository.existsById(eq(1L))).thenReturn(false);
        Assertions.assertFalse(userService.exists(1L));
    }
}
