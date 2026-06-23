package com.example.userservice;

import com.example.userservice.controller.UserController;
import com.example.userservice.model.UserDto;
import com.example.userservice.model.UserRepresentationModelAssembler;
import com.example.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = UserRepresentationModelAssembler.class))
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void testGetAllUsers() throws Exception {
        List<UserDto> userDtos = List.of(
                new UserDto("Martha", "martha@gmail.com", 33),
                new UserDto("Billy", "billy@gmail.com", 35),
                new UserDto("Jane", "jane@gmail.com", 43)
        );
        when(userService.getAllUsers()).thenReturn(userDtos);
        ResultActions resultActions = mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
        assertResponseContainsUserDtos(resultActions, userDtos);
    }

    private void assertResponseContainsUserDtos(ResultActions resultActions, List<UserDto> userDtos)
            throws Exception {
        resultActions.andExpect(jsonPath("$._embedded.userDtoList.length()", is(userDtos.size())));
        for(int i = 0; i < userDtos.size(); i++){
            UserDto userDto = userDtos.get(i);
            resultActions
                .andExpect(jsonPath(String.format("$._embedded.userDtoList[%d].name", i), is(userDto.name())))
                .andExpect(jsonPath(String.format("$._embedded.userDtoList[%d].email", i), is(userDto.email())))
                .andExpect(jsonPath(String.format("$._embedded.userDtoList[%d].age", i), is(userDto.age())));
        }
    }

    @Test
    void testGetUser() throws Exception {
        UserDto userDto = new UserDto("Martha", "martha@gmail.com", 33);
        when(userService.getUserById(eq(1L))).thenReturn(userDto);
        ResultActions resultActions = mockMvc.perform(get("/users/{userID}", 1L))
                .andExpect(status().isOk());
        assertResponseContainsUserDto(resultActions, userDto);
    }

    private void assertResponseContainsUserDto(ResultActions resultActions, UserDto userDto)
            throws Exception {
        resultActions
            .andExpect(jsonPath("$.name", is(userDto.name())))
            .andExpect(jsonPath("$.email", is(userDto.email())))
            .andExpect(jsonPath("$.age", is(userDto.age())));
    }

    @Test
    void testGetUserReturnsNotFoundHTTPStatusWhenUserDoesNotExist() throws Exception {
        String exceptionMessage = "exceptionMessage";
        when(userService.getUserById(eq(1L)))
                .thenThrow(new EntityNotFoundException(exceptionMessage));
        mockMvc.perform(get("/users/{userID}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(exceptionMessage)));
    }

    @Test
    void testCreateUser() throws Exception {
        UserDto userDto = new UserDto("Martha", "martha@gmail.com", 33);
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
        verify(userService).createUser(eq(userDto));
    }

    @Test
    void testCreateUserReturnsConflictHttpStatusWhenUserViolatesDatabaseIntegrity() throws Exception {
        UserDto userDto = new UserDto("Martha", "martha@gmail.com", 33);
        String exceptionMessage = "Details: exceptionMessage";
        doThrow(new DataIntegrityViolationException(exceptionMessage))
                .when(userService)
                .createUser(eq(userDto));
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString(exceptionMessage.substring(8))));
    }

    @ParameterizedTest
    @MethodSource("supplyNonValidUsers")
    void testCreateUserReturnsBadRequestHttpStatusWhenUserFieldsNotValid(UserDto userDto)
            throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    static Stream<UserDto> supplyNonValidUsers(){
        String bigName = "a".repeat(21);
        String bigEmail = "a".repeat(99) + "@g";
        return Stream.of(
                new UserDto("Ma", "martha@gmail.com", 33),
                new UserDto("Martha", "m@g", 33),
                new UserDto("Martha", "martha", 33),
                new UserDto("Martha", "martha@gmail.com", -1),
                new UserDto("     ", "martha@gmail.com", 33),
                new UserDto("Martha", "     ", 33),
                new UserDto(bigName, "martha@gmail.com", 33),
                new UserDto("Martha", bigEmail, 33)
        );
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto userDto = new UserDto("Martha", "martha@gmail.com", 33);
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(put("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
        verify(userService).updateUser(eq(1L), eq(userDto));
    }

    @Test
    void testUpdateUserReturnsConflictHttpStatusWhenUserViolatesDatabaseIntegrity() throws Exception {
        UserDto userDto = new UserDto("Martha", "martha@gmail.com", 33);
        String exceptionMessage = "Details: exceptionMessage";
        doThrow(new DataIntegrityViolationException(exceptionMessage))
                .when(userService)
                .updateUser(eq(1L), eq(userDto));
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(put("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString(exceptionMessage.substring(8))));
    }

    @ParameterizedTest
    @MethodSource("supplyNonValidUsers")
    void testUpdateUserReturnsBadRequestHttpStatusWhenUserFieldsNotValid(UserDto userDto)
            throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(put("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUser() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/users/{userId}", id))
                .andExpect(status().isOk());
        verify(userService).deleteUser(eq(id));
    }

    @Test
    void testDeleteUserReturnsNotFoundHTTPStatusWhenUserDoesNotExist() throws Exception {
        Long id = 1L;
        String exceptionMessage = "exceptionMessage";
        doThrow(new EntityNotFoundException(exceptionMessage))
                .when(userService)
                .deleteUser(eq(id));
        mockMvc.perform(delete("/users/{userId}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(exceptionMessage)));
    }
}
