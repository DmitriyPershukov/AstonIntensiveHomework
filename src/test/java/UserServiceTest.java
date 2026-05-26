import jakarta.persistence.EntityNotFoundException;
import model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import persistence.HibernateDao;
import service.UserService;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class UserServiceTest {

    @Mock
    private HibernateDao<User> userDao;
    private UserService userService;
    private MockitoSession mockitoSession;
    private PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private InputStream standartIn = System.in;
    @BeforeEach
    void setUp(){
        mockitoSession = Mockito.mockitoSession()
                        .initMocks(this)
                        .startMocking();
        System.setOut(new PrintStream(outputStream));
        userService = new UserService(userDao);
    }
    @AfterEach
    void tearDown(){
        mockitoSession.finishMocking();
        System.setOut(standardOut);
        System.setIn(standartIn);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L})
    void testDeleteUser(Long id){
        userService.deleteUser(id);
        Mockito.verify(userDao).deleteById(Mockito.eq(id));
    }

    @ParameterizedTest
    @MethodSource("supplyUsers")
    void testCreateUser(User user){
        userService.createUser(user);
        Mockito.verify(userDao).save(Mockito.eq(user));
    }

    static Stream<User> supplyUsers(){
        User user1 = new User("Martha", "martha@gmail.com", 35);
        User user2 = new User("John", "john@gmail.com", 44);
        return Stream.of(
            user1,
            user2
        );
    }

    @ParameterizedTest
    @MethodSource("supplyIdUsers")
    void testUpdateUser(Long id, User user){
        Mockito.when(userDao.findById(Mockito.eq(id))).thenReturn(Optional.ofNullable(user));
        userService.updateUser(id, user);
        Mockito.verify(userDao).update(Mockito.eq(user));
    }

    @ParameterizedTest
    @MethodSource("supplyIdUsers")
    void testUpdateUserThrowsExceptionWhenOldUserNotFound(Long id, User user){
        Mockito.when(userDao.findById(Mockito.eq(id))).thenReturn(Optional.empty());
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
        Mockito.when(userDao.findById(Mockito.eq(id))).thenReturn(Optional.ofNullable(user));
        Assertions.assertEquals(user, userService.getUserById(id));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L})
    void testGetUserByIdThrowsExceptionWhenOldUserNotFound(Long id){
        Mockito.when(userDao.findById(Mockito.eq(id))).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void testGetAllUsers(){
        List<User> users = supplyUsers().toList();
        Mockito.when(userDao.findAll()).thenReturn(users);
        Assertions.assertIterableEquals(users, userService.getAllUsers());
    }

    @Test
    void testExists(){
        Mockito.when(userDao.existsById(Mockito.any())).thenReturn(false);
        Mockito.when(userDao.existsById(Mockito.eq(1L))).thenReturn(true);
        Assertions.assertTrue(userService.exists(1L));
        Assertions.assertFalse(userService.exists(2L));
    }
}
