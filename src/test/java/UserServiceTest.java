import jakarta.persistence.EntityNotFoundException;
import model.User;
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
import persistence.HibernateDao;
import service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private HibernateDao<User> userDao;
    private UserService userService;
    private MockitoSession mockitoSession;

    @BeforeEach
    void setUp(){
        mockitoSession = Mockito.mockitoSession()
                        .initMocks(this)
                        .startMocking();
        userService = new UserService(userDao);
    }

    @AfterEach
    void tearDown(){
        mockitoSession.finishMocking();
    }

    @Test
    void testDeleteUser(){
        Long id = 1L;
        userService.deleteUser(id);
        verify(userDao).deleteById(eq(id));
    }

    @ParameterizedTest
    @MethodSource("supplyUsers")
    void testCreateUser(User user){
        userService.createUser(user);
        verify(userDao).save(eq(user));
    }

    static Stream<User> supplyUsers(){
        return Stream.of(
            new User("Martha", "martha@gmail.com", 35),
            new User("John", "john@gmail.com", 44)
        );
    }

    @ParameterizedTest
    @MethodSource("supplyIdUsers")
    void testUpdateUser(Long id, User user){
        when(userDao.findById(eq(id))).thenReturn(Optional.ofNullable(user));
        userService.updateUser(id, user);
        verify(userDao).update(eq(user));
    }

    @ParameterizedTest
    @MethodSource("supplyIdUsers")
    void testUpdateUserThrowsExceptionWhenOldUserNotFound(Long id, User user){
        when(userDao.findById(eq(id))).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.updateUser(id, user));
    }

    static Stream<Arguments> supplyIdUsers(){
        User user1 = new User("Martha", "martha@gmail.com", 35);
        User user2 = new User("John", "john@gmail.com", 44);
        return Stream.of(
                Arguments.of(1L, user1),
                Arguments.of(2L, user2)
        );
    }

    @ParameterizedTest
    @MethodSource("supplyIdUsers")
    void testGetUserById(Long id, User user){
        when(userDao.findById(eq(id))).thenReturn(Optional.ofNullable(user));
        Assertions.assertEquals(user, userService.getUserById(id));
    }

    @Test
    void testGetUserByIdThrowsExceptionWhenOldUserNotFound(){
        Long id = 1L;
        when(userDao.findById(eq(id))).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void testGetAllUsers(){
        List<User> users = supplyUsers().toList();
        when(userDao.findAll()).thenReturn(users);
        Assertions.assertIterableEquals(users, userService.getAllUsers());
    }

    @Test
    void testExistsReturnsTrueIfEntityFound(){
        when(userDao.existsById(eq(1L))).thenReturn(true);
        Assertions.assertTrue(userService.exists(1L));
    }

    @Test
    void testExistsReturnsFalseIfEntityNotFound(){
        when(userDao.existsById(eq(1L))).thenReturn(false);
        Assertions.assertFalse(userService.exists(1L));
    }
}
