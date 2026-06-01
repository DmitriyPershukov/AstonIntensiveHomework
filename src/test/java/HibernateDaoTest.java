import jakarta.persistence.EntityNotFoundException;
import model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import persistence.DataAccessObject;
import persistence.HibernateDao;
import persistence.SessionFactoryMaker;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Testcontainers
public class HibernateDaoTest {
    public static final String POSTGRES_IMAGE = "postgres:18";
    @Container
    private static PostgreSQLContainer<?> dbContainer =
            new PostgreSQLContainer<>(POSTGRES_IMAGE)
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("test_pw");
    private DataAccessObject<User> userDao = new HibernateDao<>(User.class);

    @BeforeAll
    static void setUp(){
        SessionFactoryMaker.setUrl(dbContainer.getJdbcUrl());
        SessionFactoryMaker.setUsername(dbContainer.getUsername());
        SessionFactoryMaker.setPassword(dbContainer.getPassword());
    }

    @BeforeEach
    void truncateTables(){
        SessionFactoryMaker.getFactory().inTransaction(session ->
                session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
                        .executeUpdate());
    }

    @Test
    void testFindById(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        Optional<User> result = userDao.findById(user.getId());
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(user, result.get());
    }

    @Test
    void testFindByIdReturnsEmptyOptionalIfEntityDoesntExist(){
        Optional<User> result = userDao.findById(1L);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll(){
        List<User> users = List.of(
            new User("John", "john@gmail.com", 35),
            new User("Mary", "mary@gmail.com", 35),
            new User("Nick", "nick@gmail.com", 35)
        );
        SessionFactoryMaker.getFactory().inTransaction(session ->
                users.forEach(user -> session.persist(user)));
        Assertions.assertIterableEquals(users, userDao.findAll());
    }

    @Test
    void testFindAllReturnsEmptyListIfTableIsEmpty(){
        Assertions.assertIterableEquals(List.of(), userDao.findAll());
    }

    @Test
    void testSave(){
        User user = new User("John", "john@gmail.com", 35);
        userDao.save(user);
        User foundUser = SessionFactoryMaker.getFactory().fromTransaction(session ->
                session.find(User.class, user.getId()));
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user, foundUser);
    }

    @ParameterizedTest
    @MethodSource("supplyExceptionUserPairs")
    void testSaveThrowsConstraintException(Class<Throwable> expectedException, User newUser){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session ->
                session.persist(user));
        Assertions.assertThrows(expectedException, () -> userDao.save(newUser));
    }

    private static Stream<Arguments> supplyExceptionUserPairs(){
        return Stream.of(
                Arguments.of(org.hibernate.exception.ConstraintViolationException.class,
                        new User("John", "george@gmail.com", 35)),
                Arguments.of(org.hibernate.exception.ConstraintViolationException.class,
                        new User("Chris", "john@gmail.com", 35)),
                Arguments.of(jakarta.validation.ConstraintViolationException.class,
                        new User("A", "a@gmail.com", 35)),
                Arguments.of(jakarta.validation.ConstraintViolationException.class,
                        new User("George", "George", 35)),
                Arguments.of(jakarta.validation.ConstraintViolationException.class,
                        new User("Mike", "mike@gmail.com", -1))
        );
    }

    private static Stream<Arguments> supplyExceptionUserUpdatedUserPairs(){
        return Stream.of(
                Arguments.of(org.hibernate.exception.ConstraintViolationException.class,
                        new User("Chris", "chris@gmail.com", 35),
                        new User("John", "chris@gmail.com", 35)),
                Arguments.of(org.hibernate.exception.ConstraintViolationException.class,
                        new User("Chris", "chris@gmail.com", 35),
                        new User("Chris", "john@gmail.com", 35)),
                Arguments.of(jakarta.validation.ConstraintViolationException.class,
                        new User("Chris", "chris@gmail.com", 35),
                        new User("A", "chris@gmail.com", 35)),
                Arguments.of(jakarta.validation.ConstraintViolationException.class,
                        new User("Chris", "chris@gmail.com", 35),
                        new User("Chris", "Chris", 35)),
                Arguments.of(jakarta.validation.ConstraintViolationException.class,
                        new User("Chris", "chris@gmail.com", 35),
                        new User("Chris", "chris@gmail.com", -1))
        );
    }

    @Test
    void testUpdate(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        user.setName("George");
        userDao.update(user);
        User foundUser = SessionFactoryMaker.getFactory().fromTransaction(session ->
                session.find(User.class, user.getId()));
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals("George", foundUser.getName());
    }

    @ParameterizedTest
    @MethodSource("supplyExceptionUserUpdatedUserPairs")
    void testUpdateThrowsConstraintException(Class<Throwable> expectedException,
                                             User newUser,
                                             User updatedUser){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(newUser));
        newUser.setName(updatedUser.getName());
        newUser.setEmail(updatedUser.getEmail());
        newUser.setAge(updatedUser.getAge());
        Assertions.assertThrows(expectedException, () -> userDao.update(newUser));
    }

    @Test
    void testDeleteById(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        userDao.deleteById(user.getId());
        Assertions.assertNull(SessionFactoryMaker.getFactory().fromTransaction(session ->
                session.find(User.class, user.getId())));
    }

    @Test
    void testDeleteByIdThrowsExceptionWhenEntityWithIdDoesntExist(){
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                userDao.deleteById(1L));
    }

    @Test
    void testExistsByIdReturnsTrueIfEntityExists(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        Assertions.assertTrue(userDao.existsById(user.getId()));
    }

    @Test
    void testExistsByIdReturnsFalseIfEntityDoesntExist(){
        Assertions.assertFalse(userDao.existsById(1L));
    }
}
